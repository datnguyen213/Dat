package com.example.gesturecontrol;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;

    // Ngưỡng cho các cử chỉ
    private static final int SHAKE_THRESHOLD = 800; // Vẫy tay
    private static final float TILT_THRESHOLD = 2.0f; // Nghiêng
    private static final float ROTATION_THRESHOLD = 1.5f; // Xoay

    // Trạng thái thiết bị
    private boolean isLightOn = false;
    private int volumeLevel = 50; // Âm lượng mặc định (0-100)
    private String currentSong = "Bài hát 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                // Tính toán thay đổi gia tốc
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                // 1. Phát hiện vẫy tay (bật/tắt đèn)
                if (speed > SHAKE_THRESHOLD) {
                    isLightOn = !isLightOn;
                    showToast("Đèn đã " + (isLightOn ? "BẬT" : "TẮT"));
                }

                // 2. Phát hiện nghiêng điện thoại (điều chỉnh âm lượng)
                if (Math.abs(y - last_y) > TILT_THRESHOLD) {
                    if (y > last_y) {
                        volumeLevel = Math.min(100, volumeLevel + 5);
                        showToast("Tăng âm lượng: " + volumeLevel + "%");
                    } else {
                        volumeLevel = Math.max(0, volumeLevel - 5);
                        showToast("Giảm âm lượng: " + volumeLevel + "%");
                    }
                }

                // 3. Phát hiện xoay điện thoại (chuyển bài hát)
                if (Math.abs(x - last_x) > ROTATION_THRESHOLD) {
                    if (x > last_x) {
                        currentSong = (currentSong.equals("Bài hát 1") ? "Bài hát 2" : "Bài hát 1");
                        showToast("Chuyển sang: " + currentSong);
                    } else {
                        currentSong = (currentSong.equals("Bài hát 1") ? "Bài hát 2" : "Bài hát 1");
                        showToast("Chuyển sang: " + currentSong);
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}