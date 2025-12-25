package com.dinerestaurant.app.ui.other;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanQRFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;

    // QR Code prefixes
    private static final String TABLE_QR_PREFIX = "TABLE:";
    private static final String RESERVATION_QR_PREFIX = "RESERVATION:";

    private PreviewView previewView;
    private View scanLine;

    private ExecutorService cameraExecutor;
    private boolean isScanning = true;

    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private ProcessCameraProvider cameraProvider;

    public ScanQRFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan_qr, container, false);

        previewView = root.findViewById(R.id.previewView);
        scanLine = root.findViewById(R.id.scanLine);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        // Setup demo buttons cho testing trên emulator
        setupDemoButtons(root);

        return root;
    }

    /**
     * Setup các nút demo để test trên emulator
     */
    private void setupDemoButtons(View root) {
        // Demo quét bàn 1
        root.findViewById(R.id.btnDemoTable1).setOnClickListener(v -> {
            processQRCode("TABLE:1");
        });

        // Demo quét bàn 2
        root.findViewById(R.id.btnDemoTable2).setOnClickListener(v -> {
            processQRCode("TABLE:2");
        });

        // Demo quét bàn 3
        root.findViewById(R.id.btnDemoTable3).setOnClickListener(v -> {
            processQRCode("TABLE:3");
        });

        // Demo quét mã đặt bàn
        root.findViewById(R.id.btnDemoReservation).setOnClickListener(v -> {
            processQRCode("RESERVATION:1");
        });
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Cần cấp quyền Camera để quét mã QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider
                .getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient(options);

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    android.media.Image mediaImage = imageProxy.getImage();

                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(mediaImage,
                                imageProxy.getImageInfo().getRotationDegrees());

                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    if (!isScanning)
                                        return;

                                    for (Barcode barcode : barcodes) {
                                        String value = barcode.getRawValue();
                                        if (value != null) {
                                            isScanning = false;
                                            processQRCode(value);
                                        }
                                    }
                                })
                                .addOnFailureListener(Throwable::printStackTrace)
                                .addOnCompleteListener(task -> imageProxy.close());
                    } else {
                        imageProxy.close();
                    }
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    /**
     * Xử lý mã QR đã quét
     */
    private void processQRCode(String qrValue) {
        requireActivity().runOnUiThread(() -> {
            if (qrValue.startsWith(TABLE_QR_PREFIX)) {
                // QR của bàn - chứa table_id
                String tableId = qrValue.substring(TABLE_QR_PREFIX.length());
                showOrderTypeDialog(tableId);
            } else if (qrValue.startsWith(RESERVATION_QR_PREFIX)) {
                // QR của đặt bàn - chứa reservation_id
                String reservationId = qrValue.substring(RESERVATION_QR_PREFIX.length());
                showReservationInfo(reservationId);
            } else {
                // QR không nhận dạng được
                showUnknownQRDialog(qrValue);
            }
        });
    }

    /**
     * Navigate đến màn hình chọn phương thức order
     */
    private void showOrderTypeDialog(String tableId) {
        try {
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putString("table_id", tableId);
            bundle.putString("table_name", "Bàn " + tableId);

            // Navigate đến QROrderTypeFragment
            navController.navigate(R.id.action_scanQRFragment_to_qrOrderTypeFragment, bundle);

        } catch (Exception e) {
            // Fallback: hiển thị dialog nếu không navigate được
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("🍽 Bàn " + tableId)
                    .setMessage("Bạn muốn:")
                    .setPositiveButton("Ăn tại chỗ", (dialog, which) -> {
                        navigateToMenu("Tại chỗ", tableId, null);
                    })
                    .setNegativeButton("Mang về", (dialog, which) -> {
                        navigateToMenu("Mang về", null, null);
                    })
                    .setNeutralButton("Hủy", (dialog, which) -> {
                        isScanning = true;
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * Hiển thị thông tin đặt bàn (cho nhân viên check-in)
     */
    private void showReservationInfo(String reservationId) {
        // TODO: Gọi API lấy thông tin reservation
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("📋 Thông tin đặt bàn")
                .setMessage("Mã đặt bàn: " + reservationId + "\n\n(Tính năng check-in sẽ được cập nhật)")
                .setPositiveButton("Đóng", (dialog, which) -> isScanning = true)
                .setCancelable(false)
                .show();
    }

    /**
     * QR không nhận dạng được
     */
    private void showUnknownQRDialog(String qrValue) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Mã QR")
                .setMessage(qrValue)
                .setPositiveButton("Đóng", (dialog, which) -> isScanning = true)
                .setNegativeButton("Quét lại", (dialog, which) -> isScanning = true)
                .setCancelable(false)
                .show();
    }

    /**
     * Chuyển đến màn hình menu/order
     * 
     * @param orderType "Tại chỗ" hoặc "Mang về"
     * @param tableId   ID bàn (nếu ăn tại chỗ)
     * @param addressId ID địa chỉ (nếu mang về)
     */
    private void navigateToMenu(String orderType, String tableId, String addressId) {
        try {
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putString("order_type", orderType);
            if (tableId != null) {
                bundle.putString("table_id", tableId);
            }
            if (addressId != null) {
                bundle.putString("address_id", addressId);
            }

            // Navigate đến HomeFragment (màn hình chọn món)
            navController.navigate(R.id.homeFragment, bundle);

            Toast.makeText(requireContext(),
                    orderType.equals("Tại chỗ") ? "Đang order cho Bàn " + tableId : "Đang chọn món mang về",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isScanning = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null)
            cameraExecutor.shutdown();
        if (cameraProvider != null)
            cameraProvider.unbindAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reset scanning khi quay lại
        isScanning = true;
    }
}