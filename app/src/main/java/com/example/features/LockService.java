package com.example.features;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.annotation.SuppressLint;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class LockService extends Service {

    private SensorManager sm;
    private DevicePolicyManager mdevicePolicy;


    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toast.makeText(this, "SecondService Created!", Toast.LENGTH_LONG).show();
        mdevicePolicy = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        super.onCreate();

    }
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @SuppressLint("InvalidWakeLockTag")
        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;
            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];
            float norm_Of_g =(float) Math.sqrt(x * x + y * y + z * z);

            // Normalize the accelerometer vector
            x = (x / norm_Of_g);
            y = (y / norm_Of_g);
            z = (z / norm_Of_g);
            int inclination = (int) Math.round(Math.toDegrees(Math.acos(z)));
            Log.i("tag","incline is:"+inclination);

            if (inclination < 25 || inclination > 155)
            {

                mdevicePolicy.lockNow();
                Toast.makeText(getApplicationContext(), "device flat - beep!",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SecondService Started!", Toast.LENGTH_LONG).show();

        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "SecondService Destroyed!", Toast.LENGTH_LONG).show();
        sm.unregisterListener(sensorEventListener);
        super.onDestroy();
    }


}
