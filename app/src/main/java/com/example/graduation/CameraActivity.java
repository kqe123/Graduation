package com.example.graduation;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import androidx.camera.lifecycle.ProcessCameraProvider;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //androidmanifest.xml파일 뿐만아니라  런타임에서도 권한을 설정해줘야한다.
    private static final int REQUEST_CODE_PERMISSIONS = 101; // 카메라 권한을 요청하기 위한 요청 코드
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    private LifecycleOwner lifecycleOwner = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (allPermissionsGranted()) { // 모든 권한이 허락되면 카메라 사용, 아니면 권한 허용 묻기
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS); //
        }

        Button captureBtn = findViewById(R.id.capturebtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "capture_" + timeStamp + ".jpg";
                File file = new File(getFilesDir(), fileName);
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions,ContextCompat.getMainExecutor(CameraActivity.this),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                // insert your code here.

                                Log.d(TAG+ " onImageSavedgetSavedUri", String.valueOf(outputFileResults.getSavedUri()));
                                Toast.makeText(getApplicationContext(), fileName,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CameraActivity.this, CaptureActivity.class);
                                intent.putExtra("fileName", fileName);
                                startActivity(intent);
                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                                // insert your code here.
                                Log.d(" Camerror", String.valueOf(error));
                                Toast.makeText(getApplicationContext(), "캡쳐 실패",Toast.LENGTH_LONG).show();

                            }
                        }
                );
            }
        });
    }



    // 프리뷰를 보여주는 메서드
    void bindPreview( @NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        PreviewView previewView = findViewById(R.id.previewView);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
            imageCapture =
                    new ImageCapture.Builder()
                            .build();
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview);
        //Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }

    // 모든 권한이 허용되었는지 체크하는 메서드
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다! 앱 정보에 들어가서 카메라 권한을 허용하세요!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        //CameraProview 사용 가능 여부 확인
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        //CameraProview 사용 가능 여부 확인
    }
}