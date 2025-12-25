package com.dinerestaurant.app.ui.reservation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Activity hiển thị thông tin đặt bàn thành công và QR code cho check-in
 */
public class ReservationSuccessActivity extends AppCompatActivity {

    // Intent extras keys
    public static final String EXTRA_RESERVATION_ID = "reservation_id";
    public static final String EXTRA_TABLE_NAME = "table_name";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_GUEST_COUNT = "guest_count";

    private ImageView ivQRCode;
    private TextView tvReservationCode;
    private TextView tvTableName;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvGuestCount;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_success);

        initViews();
        loadDataFromIntent();
    }

    private void initViews() {
        ivQRCode = findViewById(R.id.ivQRCode);
        tvReservationCode = findViewById(R.id.tvReservationCode);
        tvTableName = findViewById(R.id.tvTableName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        btnDone = findViewById(R.id.btnDone);

        btnDone.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void loadDataFromIntent() {
        // Lấy dữ liệu từ Intent
        int reservationId = getIntent().getIntExtra(EXTRA_RESERVATION_ID, 0);
        String tableName = getIntent().getStringExtra(EXTRA_TABLE_NAME);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String time = getIntent().getStringExtra(EXTRA_TIME);
        int guestCount = getIntent().getIntExtra(EXTRA_GUEST_COUNT, 0);

        // Tạo mã QR
        String qrContent = "RESERVATION:" + reservationId;
        tvReservationCode.setText(qrContent);

        // Generate QR Code
        generateQRCode(qrContent);

        // Hiển thị thông tin
        tvTableName.setText(tableName != null ? tableName : "N/A");
        tvDate.setText(date != null ? date : "N/A");
        tvTime.setText(time != null ? time : "N/A");
        tvGuestCount.setText(guestCount + " người");
    }

    /**
     * Tạo QR code bitmap từ text
     */
    private void generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF333333 : 0xFFFFFFFF);
                }
            }

            ivQRCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
            // Fallback: hiển thị text thay vì QR
            ivQRCode.setImageResource(R.drawable.ic_qr_placeholder);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
