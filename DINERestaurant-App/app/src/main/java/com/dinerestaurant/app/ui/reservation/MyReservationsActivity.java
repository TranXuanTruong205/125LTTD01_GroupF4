package com.dinerestaurant.app.ui.reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.repository.ReservationRepository;
import com.dinerestaurant.app.model.ReservationItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReservationsActivity extends AppCompatActivity
        implements ReservationAdapter.OnReservationActionListener {

    private ImageView btnBack;
    private RecyclerView rvReservations;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private MaterialButton btnBookNow;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton fabNewReservation;

    private ReservationRepository repository;
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        initViews();
        repository = new ReservationRepository(this);
        setupRecyclerView();
        setupListeners();
        loadReservations();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rvReservations = findViewById(R.id.rv_reservations);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnBookNow = findViewById(R.id.btn_book_now);
        progressBar = findViewById(R.id.progress_bar);
        fabNewReservation = findViewById(R.id.fab_new_reservation);
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter(this, this);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        swipeRefresh.setOnRefreshListener(this::loadReservations);
        swipeRefresh.setColorSchemeResources(R.color.orange);

        // Nút đặt bàn trong empty state
        btnBookNow.setOnClickListener(v -> {
            startActivity(new Intent(this, ReservationActivity.class));
        });

        // FAB đặt bàn mới (luôn hiển thị)
        fabNewReservation.setOnClickListener(v -> {
            startActivity(new Intent(this, ReservationActivity.class));
        });
    }

    private void loadReservations() {
        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        layoutEmpty.setVisibility(View.GONE);

        repository.getMyReservations().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    Boolean success = (Boolean) res.get("success");

                    if (Boolean.TRUE.equals(success)) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");

                        if (data != null && !data.isEmpty()) {
                            List<ReservationItem> reservations = parseReservations(data);
                            adapter.setReservations(reservations);
                            showList();
                        } else {
                            showEmpty();
                        }
                    } else {
                        String message = (String) res.get("message");
                        Toast.makeText(MyReservationsActivity.this,
                                message != null ? message : "Lỗi tải dữ liệu",
                                Toast.LENGTH_SHORT).show();
                        showEmpty();
                    }
                } else {
                    Toast.makeText(MyReservationsActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    showEmpty();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MyReservationsActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                showEmpty();
            }
        });
    }

    private List<ReservationItem> parseReservations(List<Map<String, Object>> data) {
        List<ReservationItem> list = new ArrayList<>();

        for (Map<String, Object> map : data) {
            ReservationItem item = new ReservationItem();

            // Reservation ID
            Object idObj = map.get("reservationId");
            if (idObj instanceof Number) {
                item.setReservationId(((Number) idObj).intValue());
            }

            // Table ID
            Object tableIdObj = map.get("tableId");
            if (tableIdObj instanceof Number) {
                item.setTableId(((Number) tableIdObj).intValue());
            }

            // Table info (từ nested object nếu có)
            Object tableObj = map.get("table");
            if (tableObj instanceof Map) {
                Map<String, Object> tableMap = (Map<String, Object>) tableObj;
                Object tableNumber = tableMap.get("tableNumber");
                if (tableNumber != null) {
                    item.setTableNumber(tableNumber.toString());
                }
                Object capacity = tableMap.get("capacity");
                if (capacity instanceof Number) {
                    item.setTableCapacity(((Number) capacity).intValue());
                }
            }

            // Date
            Object dateObj = map.get("reservationDate");
            if (dateObj != null) {
                item.setReservationDate(dateObj.toString());
            }

            // Time
            Object timeObj = map.get("reservationTime");
            if (timeObj != null) {
                item.setReservationTime(timeObj.toString());
            }

            // Guest count
            Object guestObj = map.get("guestCount");
            if (guestObj instanceof Number) {
                item.setGuestCount(((Number) guestObj).intValue());
            }

            // Note
            Object noteObj = map.get("note");
            if (noteObj != null) {
                item.setNote(noteObj.toString());
            }

            // Status
            Object statusObj = map.get("status");
            if (statusObj != null) {
                item.setStatus(statusObj.toString());
            }

            // QR Code
            Object qrObj = map.get("qrCode");
            if (qrObj != null) {
                item.setQrCode(qrObj.toString());
            }

            list.add(item);
        }

        return list;
    }

    private void showList() {
        rvReservations.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        rvReservations.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelClick(ReservationItem reservation, int position) {
        // Hiển thị dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc muốn hủy đặt bàn này không?")
                .setPositiveButton("Hủy đặt bàn", (dialog, which) -> {
                    cancelReservation(reservation, position);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onEditClick(ReservationItem reservation, int position) {
        showEditDialog(reservation, position);
    }

    /**
     * Hiển thị dialog sửa đặt bàn
     */
    private void showEditDialog(ReservationItem reservation, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_reservation, null);

        // Bind views
        com.google.android.material.textfield.TextInputEditText edtDate = dialogView.findViewById(R.id.edt_edit_date);
        com.google.android.material.textfield.TextInputEditText edtTime = dialogView.findViewById(R.id.edt_edit_time);
        com.google.android.material.textfield.TextInputEditText edtGuestCount = dialogView
                .findViewById(R.id.edt_edit_guest_count);
        com.google.android.material.textfield.TextInputEditText edtNote = dialogView.findViewById(R.id.edt_edit_note);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel_edit);
        com.google.android.material.button.MaterialButton btnSave = dialogView.findViewById(R.id.btn_save_edit);

        // Fill current data
        edtDate.setText(reservation.getReservationDate());
        edtTime.setText(reservation.getReservationTime());
        edtGuestCount.setText(String.valueOf(reservation.getGuestCount()));
        edtNote.setText(reservation.getNote());

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Date picker
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        edtDate.setOnClickListener(v -> {
            new android.app.DatePickerDialog(this, (view, year, month, day) -> {
                String dateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                edtDate.setText(dateStr);
            }, calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker
        edtTime.setOnClickListener(v -> {
            new android.app.TimePickerDialog(this, (view, hour, minute) -> {
                String timeStr = String.format(java.util.Locale.US, "%02d:%02d", hour, minute);
                edtTime.setText(timeStr);
            }, calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE), true).show();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Save button
        btnSave.setOnClickListener(v -> {
            String date = edtDate.getText() != null ? edtDate.getText().toString().trim() : "";
            String time = edtTime.getText() != null ? edtTime.getText().toString().trim() : "";
            String guestStr = edtGuestCount.getText() != null ? edtGuestCount.getText().toString().trim() : "";
            String note = edtNote.getText() != null ? edtNote.getText().toString().trim() : "";

            if (date.isEmpty() || time.isEmpty() || guestStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build request
            java.util.Map<String, Object> request = new java.util.HashMap<>();
            request.put("tableId", reservation.getTableId());
            request.put("reservationDate", date);
            request.put("reservationTime", time);
            request.put("guestCount", Integer.parseInt(guestStr));
            request.put("note", note);

            // Show loading
            btnSave.setEnabled(false);
            btnSave.setText("Đang lưu...");

            // Call API
            repository.updateReservation(reservation.getReservationId(), request)
                    .enqueue(new Callback<java.util.Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<java.util.Map<String, Object>> call,
                                Response<java.util.Map<String, Object>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Boolean success = (Boolean) response.body().get("success");
                                if (Boolean.TRUE.equals(success)) {
                                    Toast.makeText(MyReservationsActivity.this,
                                            "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    loadReservations(); // Refresh list
                                } else {
                                    String msg = (String) response.body().get("message");
                                    Toast.makeText(MyReservationsActivity.this,
                                            msg != null ? msg : "Cập nhật thất bại",
                                            Toast.LENGTH_LONG).show();
                                    btnSave.setEnabled(true);
                                    btnSave.setText("💾 Lưu");
                                }
                            } else {
                                Toast.makeText(MyReservationsActivity.this,
                                        "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                                btnSave.setEnabled(true);
                                btnSave.setText("💾 Lưu");
                            }
                        }

                        @Override
                        public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                            Toast.makeText(MyReservationsActivity.this,
                                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            btnSave.setEnabled(true);
                            btnSave.setText("💾 Lưu");
                        }
                    });
        });

        dialog.show();
    }

    private void cancelReservation(ReservationItem reservation, int position) {
        progressBar.setVisibility(View.VISIBLE);

        repository.cancelReservation(reservation.getReservationId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        Toast.makeText(MyReservationsActivity.this,
                                "Đã hủy đặt bàn thành công!",
                                Toast.LENGTH_SHORT).show();
                        // Cập nhật UI - có thể xóa item hoặc đổi status
                        adapter.updateItemStatus(position, "Đã hủy");
                    } else {
                        String message = (String) response.body().get("message");
                        Toast.makeText(MyReservationsActivity.this,
                                message != null ? message : "Không thể hủy đặt bàn",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyReservationsActivity.this,
                            "Lỗi hủy đặt bàn",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyReservationsActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh khi quay lại từ màn đặt bàn
        loadReservations();
    }
}
