package com.example.features;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

public class AlarmActivity1 extends AppCompatActivity {
    private int amout_of_seconds = 3;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private int speed = 4;
    TextView alarmSpeed;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm1);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        alarmSpeed = findViewById(R.id.speedtxt);
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("MySharedPref", 100);
        this.speed = sh.getInt("speed", 20);
        alarmSpeed.setText(alarmSpeed.getText() + String.valueOf(this.speed));


        Uri my_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        final Ringtone ringtone = RingtoneManager.getRingtone(this, my_uri);
        ringtone.play();
        vibrator.vibrate(1000 * amout_of_seconds);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[2] > 0.5f * speed / 20 || event.values[2] < -0.5f * speed / 20) {
                    vibrator.cancel();
                    ringtone.stop();
                    sensorManager.unregisterListener(sensorEventListener);
                    finish();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_NORMAL);
    }
}
