package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;

import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;

public class ScannerActivity extends BaseActivity {

    private Fotoapparat fotoapparat;
    private CameraView cameraView;
    private BarcodeScanner scanner;
    private View progress;
    private static final int RESCAN_THRESHOLD = 20;
    private int rescanCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initView();
        handleClicks();
    }

    private void initView() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_QR_CODE).build();

        scanner = BarcodeScanning.getClient(options);

        progress = findViewById(R.id.progress);

        cameraView = findViewById(R.id.cameraView);

        fotoapparat = Fotoapparat.with(this)
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .build();
    }

    private void handleClicks() {
        findViewById(R.id.button_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        fotoapparat.takePicture().toBitmap().whenDone(new WhenDoneListener<BitmapPhoto>() {
            @Override
            public void whenDone(BitmapPhoto bitmapPhoto) {
                scanImage(bitmapPhoto);
            }
        });
    }

    private void scanImage(BitmapPhoto bitmapPhoto) {
        InputImage inputImage = InputImage.fromBitmap(bitmapPhoto.bitmap, bitmapPhoto.rotationDegrees);
        scanBarcodes(inputImage);
    }

    private void scanBarcodes(InputImage image) {
        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.isEmpty()) {
                            if (rescanCount < RESCAN_THRESHOLD) {
                                rescanCount++;
                                takePicture();
                            } else {
                                showErrorDialog("No QR codes scanned");
                            }
                            return;
                        }

                        String scannedBarcode = barcodes.get(0).getRawValue();
                        if (scannedBarcode.isEmpty()) {
                            if (rescanCount < RESCAN_THRESHOLD) {
                                rescanCount++;
                                takePicture();
                            } else {
                                showErrorDialog("No QR codes scanned");
                            }
                            return;
                        }

                        Intent data = new Intent();
                        data.putExtra("qr", scannedBarcode);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorDialog("QR code scan failure");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fotoapparat.stop();
    }

}