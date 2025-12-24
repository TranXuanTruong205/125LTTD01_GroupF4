package com.dinerestaurant.app.ui.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.TableItem;
import com.dinerestaurant.app.data.repository.ReservationRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity implements TableAdapter.OnTableClickListener {

    // Views
    private ImageView btnBack, btnMyReservations;
    private TextInputEditText edtDate, edtTime, edtGuestCount, edtNote;
    private MaterialButton btnCheck, btnBook;
    private RecyclerView rvTables;
    private TextView tvNoTable;
    private ProgressBar progressBar;
    private CardView cardTables, cardBooking;
    private LinearLayout layoutSelectedTable;
    private TextView tvSelectedTableName, tvSelectedTableCapacity;

    // Data
    private ReservationRepository repository;
    private TableAdapter tableAdapter;
    private TableItem selectedTable;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initViews();
        repository = new ReservationRepository(this);
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnMyReservations = findViewById(R.id.btn_my_reservations);
        edtDate = findViewById(R.id.edt_date);
        edtTime = findViewById(R.id.edt_time);
        edtGuestCount = findViewById(R.id.edt_guest_count);
        edtNote = findViewById(R.id.edt_note);
        btnCheck = findViewById(R.id.btn_check_availability);
        btnBook = findViewById(R.id.btn_book_table);
        rvTables = findViewById(R.id.rv_tables);
        tvNoTable = findViewById(R.id.tv_no_table);
        progressBar = findViewById(R.id.progress_bar);
        cardTables = findViewById(R.id.card_tables);
        cardBooking = findViewById(R.id.card_booking);
        layoutSelectedTable = findViewById(R.id.layout_selected_table);
        tvSelectedTableName = findViewById(R.id.tv_selected_table_name);
        tvSelectedTableCapacity = findViewById(R.id.tv_selected_table_capacity);
    }

    private void setupRecyclerView() {
        tableAdapter = new TableAdapter(this, this);
        rvTables.setLayoutManager(new GridLayoutManager(this, 2));
        rvTables.setAdapter(tableAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Nút xem lịch đặt bàn
        btnMyReservations.setOnClickListener(v -> {
            startActivity(new Intent(this, MyReservationsActivity.class));
        });

        edtDate.setOnClickListener(v -> showDatePicker());
        edtTime.setOnClickListener(v -> showTimePicker());
        btnCheck.setOnClickListener(v -> checkTables());
        btnBook.setOnClickListener(v -> bookTable());
    }

    private void showDatePicker() {
        // Cài đặt ngày tối thiểu là hôm nay
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
                    edtDate.setText(dateStr);

                    // Reset các lựa chọn trước đó khi thay đổi ngày
                    resetTableSelection();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showTimePicker() {
        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    edtTime.setText(String.format(Locale.US, "%02d:%02d", hour, minute));

                    // Reset các lựa chọn trước đó khi thay đổi giờ
                    resetTableSelection();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show();
    }

    private void resetTableSelection() {
        selectedTable = null;
        tableAdapter.clearSelection();
        layoutSelectedTable.setVisibility(View.GONE);
        btnBook.setEnabled(false);
    }

    private void checkTables() {
        String date = edtDate.getText() != null ? edtDate.getText().toString().trim() : "";
        String time = edtTime.getText() != null ? edtTime.getText().toString().trim() : "";
        String guestStr = edtGuestCount.getText() != null ? edtGuestCount.getText().toString().trim() : "";

        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (guestStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số khách", Toast.LENGTH_SHORT).show();
            return;
        }

        int guestCount;
        try {
            guestCount = Integer.parseInt(guestStr);
            if (guestCount <= 0) {
                Toast.makeText(this, "Số khách phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số khách không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        resetTableSelection();

        repository.getAvailableTables(date, time, guestCount).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    Boolean success = (Boolean) res.get("success");

                    if (Boolean.TRUE.equals(success)) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");

                        if (data != null && !data.isEmpty()) {
                            List<TableItem> tables = new ArrayList<>();

                            for (Map<String, Object> map : data) {
                                TableItem table = new TableItem();

                                Object tableIdObj = map.get("tableId");
                                if (tableIdObj instanceof Number) {
                                    table.setTableId(((Number) tableIdObj).intValue());
                                }

                                Object tableNumberObj = map.get("tableNumber");
                                if (tableNumberObj != null) {
                                    table.setTableNumber(tableNumberObj.toString());
                                }

                                Object capacityObj = map.get("capacity");
                                if (capacityObj instanceof Number) {
                                    table.setCapacity(((Number) capacityObj).intValue());
                                }

                                table.setStatus("available");
                                tables.add(table);
                            }

                            tableAdapter.setTables(tables);
                            cardTables.setVisibility(View.VISIBLE);
                            cardBooking.setVisibility(View.VISIBLE);
                            tvNoTable.setVisibility(View.GONE);
                            rvTables.setVisibility(View.VISIBLE);
                        } else {
                            showNoTablesAvailable();
                        }
                    } else {
                        String message = (String) res.get("message");
                        Toast.makeText(ReservationActivity.this,
                                message != null ? message : "Không có bàn trống",
                                Toast.LENGTH_SHORT).show();
                        showNoTablesAvailable();
                    }
                } else {
                    Toast.makeText(ReservationActivity.this, "Lỗi kiểm tra bàn", Toast.LENGTH_SHORT).show();
                    showNoTablesAvailable();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ReservationActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNoTablesAvailable() {
        cardTables.setVisibility(View.VISIBLE);
        cardBooking.setVisibility(View.GONE);
        tvNoTable.setVisibility(View.VISIBLE);
        rvTables.setVisibility(View.GONE);
    }

    @Override
    public void onTableClick(TableItem table) {
        selectedTable = table;

        // Hiển thị thông tin bàn đã chọn
        layoutSelectedTable.setVisibility(View.VISIBLE);
        tvSelectedTableName.setText("Bàn số " + table.getTableNumber());
        tvSelectedTableCapacity.setText("Sức chứa: " + table.getCapacity() + " người");

        // Kích hoạt nút đặt bàn
        btnBook.setEnabled(true);

        Toast.makeText(this, "Đã chọn bàn " + table.getTableNumber(), Toast.LENGTH_SHORT).show();
    }

    private void bookTable() {
        if (selectedTable == null) {
            Toast.makeText(this, "Vui lòng chọn bàn", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = edtDate.getText() != null ? edtDate.getText().toString().trim() : "";
        String time = edtTime.getText() != null ? edtTime.getText().toString().trim() : "";
        String guestStr = edtGuestCount.getText() != null ? edtGuestCount.getText().toString().trim() : "";
        String note = edtNote.getText() != null ? edtNote.getText().toString().trim() : "";

        Map<String, Object> request = new HashMap<>();
        request.put("tableId", selectedTable.getTableId());
        request.put("reservationDate", date);
        request.put("reservationTime", time);
        request.put("guestCount", Integer.parseInt(guestStr));
        request.put("note", note);

        showLoading(true);
        btnBook.setEnabled(false);

        repository.createReservation(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        Toast.makeText(ReservationActivity.this,
                                "🎉 Đặt bàn thành công!",
                                Toast.LENGTH_LONG).show();

                        // Đóng màn hình và quay lại
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String msg = (String) response.body().get("message");
                        Toast.makeText(ReservationActivity.this,
                                msg != null ? msg : "Đặt bàn thất bại",
                                Toast.LENGTH_LONG).show();
                        btnBook.setEnabled(true);
                    }
                } else {
                    Toast.makeText(ReservationActivity.this, "Lỗi đặt bàn", Toast.LENGTH_SHORT).show();
                    btnBook.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                btnBook.setEnabled(true);
                Toast.makeText(ReservationActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCheck.setEnabled(!show);
    }
}