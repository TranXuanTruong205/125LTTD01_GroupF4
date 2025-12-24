package com.dinerestaurant.app.ui.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.TableItem; // sửa đường dẫn nếu TableItem ở model
import com.dinerestaurant.app.data.repository.ReservationRepository;

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

public class ReservationActivity extends AppCompatActivity implements TableGridAdapter.OnTableSelectedListener {

    private EditText edtDate, edtTime, edtGuestCount, edtNote;
    private Button btnCheck, btnBook;
    private GridView gvTables;
    private TextView tvNoTable;
    private ProgressBar progressBar;

    private ReservationRepository repository;
    private List<TableItem> tables = new ArrayList<>();
    private TableGridAdapter adapter;
    private TableItem selectedTable;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initViews();
        repository = new ReservationRepository();
        adapter = new TableGridAdapter(this, tables, this);
        gvTables.setAdapter(adapter);

        setupListeners();
    }

    private void initViews() {
        edtDate = findViewById(R.id.edt_date);
        edtTime = findViewById(R.id.edt_time);
        edtGuestCount = findViewById(R.id.edt_guest_count);
        edtNote = findViewById(R.id.edt_note);
        btnCheck = findViewById(R.id.btn_check_availability);
        btnBook = findViewById(R.id.btn_book_table);
        gvTables = findViewById(R.id.gv_tables);
        tvNoTable = findViewById(R.id.tv_no_table);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        edtDate.setOnClickListener(v -> showDatePicker());
        edtTime.setOnClickListener(v -> showTimePicker());
        btnCheck.setOnClickListener(v -> checkTables());
        btnBook.setOnClickListener(v -> bookTable());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            edtDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            edtTime.setText(String.format(Locale.US, "%02d:%02d", hour, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void checkTables() {
        String date = edtDate.getText().toString().trim();
        String time = edtTime.getText().toString().trim();
        String guestStr = edtGuestCount.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || guestStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int guestCount = Integer.parseInt(guestStr);

        showLoading(true);

        repository.getAvailableTables(date, time, guestCount).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    boolean success = (boolean) res.get("success");
                    if (success) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");
                        tables.clear();
                        for (Map<String, Object> map : data) {
                            TableItem table = new TableItem();
                            table.setTableId(((Number) map.get("tableId")).intValue());
                            table.setTableNumber((String) map.get("tableNumber"));
                            table.setCapacity(((Number) map.get("capacity")).intValue());
                            table.setStatus("available");
                            tables.add(table);
                        }
                        adapter.notifyDataSetChanged();
                        tvNoTable.setVisibility(tables.isEmpty() ? View.VISIBLE : View.GONE);
                        gvTables.setVisibility(tables.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        Toast.makeText(ReservationActivity.this, "Không có bàn trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReservationActivity.this, "Lỗi kiểm tra bàn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ReservationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onTableSelected(TableItem table) {
        selectedTable = table;
        btnBook.setEnabled(true);
        Toast.makeText(this, "Đã chọn bàn " + table.getTableNumber(), Toast.LENGTH_SHORT).show();
    }

    private void bookTable() {
        if (selectedTable == null) {
            Toast.makeText(this, "Vui lòng chọn bàn", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("tableId", selectedTable.getTableId());
        request.put("reservationDate", edtDate.getText().toString().trim());
        request.put("reservationTime", edtTime.getText().toString().trim());
        request.put("guestCount", Integer.parseInt(edtGuestCount.getText().toString().trim()));
        request.put("note", edtNote.getText().toString().trim());

        showLoading(true);

        repository.createReservation(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = (boolean) response.body().get("success");
                    if (success) {
                        Toast.makeText(ReservationActivity.this, "Đặt bàn thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String msg = (String) response.body().get("message");
                        Toast.makeText(ReservationActivity.this, msg != null ? msg : "Đặt bàn thất bại", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ReservationActivity.this, "Lỗi đặt bàn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ReservationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCheck.setEnabled(!show);
        btnBook.setEnabled(!show && selectedTable != null);
    }
}