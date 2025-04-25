package com.example.bai1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private ImageView imageView;
    private Uri photoURI;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCapture = findViewById(R.id.btnCapture);
        imageView = findViewById(R.id.imageView);

        btnCapture. setOnClickListener(v -> checkCameraPermission());
    }

    // Kiểm tra quyền CAMERA
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else{
            openCamera();
        }
    }

    // Xử Lý kết quả cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Quyền Camera bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Mở Camera
//    private void openCamera() {
//        Intent intent = new Intent();
//        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "Không tìm thấy ứng dụng Camera!", Toast.LENGTH_SHORT).show();
//        }
//    }
    private void openCamera() {
        try {
            photoFile = createImageFile();
            photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile); // Sử dụng FileProvider

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // Cấp quyền cho camera

            if (intent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(intent); // Sử dụng cameraLauncher để mở camera
            } else {
                Toast.makeText(this, "Không tìm thấy ứng dụng Camera!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi tạo file ảnh!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Nhận kết quả từ Camera
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d("PhotoURI", String.valueOf(photoURI));
                        imageView.setImageURI(photoURI);
                        Toast.makeText(MainActivity.this, "Ảnh đã luu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Chụp ảnh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Tạo file ảnh và lưu vào bộ nhớ
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}