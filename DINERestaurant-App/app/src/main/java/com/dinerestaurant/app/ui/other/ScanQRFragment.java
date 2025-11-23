package com.dinerestaurant.app.ui.other;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.dinerestaurant.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
// IMPORT QUAN TRỌNG: Lưu ý đường dẫn 'common' này
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

    private PreviewView previewView;
    private View scanLine; // Đảm bảo bạn có view này trong layout XML hoặc xóa dòng này nếu không cần

    private ExecutorService cameraExecutor;
    private boolean isScanning = true;

    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private ProcessCameraProvider cameraProvider;

    public ScanQRFragment() {}

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

        return root;
    }

    // Kiểm tra quyền
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // Xin quyền Camera
    private void requestCameraPermission() {
        // Sử dụng requestPermissions của Fragment thay vì ActivityCompat để nhận callback tại đây
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }

    // Xử lý khi người dùng bấm Cho phép/Từ chối
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã cho phép, bắt đầu camera ngay
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Cần cấp quyền Camera để quét mã QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Cấu hình chỉ quét QR Code
                BarcodeScannerOptions options =
                        new BarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                .build();

                BarcodeScanner scanner = BarcodeScanning.getClient(options);

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
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
                                    if (!isScanning) return;

                                    for (Barcode barcode : barcodes) {
                                        String value = barcode.getRawValue();
                                        if (value != null) {
                                            isScanning = false; // Dừng quét để không hiện dialog liên tục
                                            showResultDialog(value);
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> e.printStackTrace())
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
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void showResultDialog(String link) {
        // Đảm bảo chạy trên UI Thread
        requireActivity().runOnUiThread(() -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Kết quả QR")
                    .setMessage(link)
                    .setPositiveButton("Mở link", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(requireContext(),
                                    "Link không hợp lệ!", Toast.LENGTH_SHORT).show();
                        }
                        // Sau khi mở link, cho phép quét lại nếu quay lại app
                        isScanning = true;
                    })
                    .setNegativeButton("Quét lại", (dialog, which) -> isScanning = true)
                    .setCancelable(false)
                    .show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) cameraExecutor.shutdown();
        if (cameraProvider != null) cameraProvider.unbindAll();
    }
}