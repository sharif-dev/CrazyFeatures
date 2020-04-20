package com.example.features;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;

import androidx.annotation.RequiresApi;

import static android.content.Intent.getIntent;

public class ShakeService extends Service{
    private SensorManager sm;
    private float acelval;
    private float acellast;
    private float shake;
    private int alpha_;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate() {

        Toast.makeText(this, "Service Created!", Toast.LENGTH_LONG).show();
        super.onCreate();

    }
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @SuppressLint("InvalidWakeLockTag")
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acellast = acelval;
            acelval = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = acelval - acellast;
            shake = shake * 0.9f + delta;

            if (shake > alpha_) {

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isInteractive();

                if (isScreenOn == false)
                {
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
                    wl.acquire();
                    wl.release();
                }
                Toast toast = Toast.makeText(getApplicationContext(), "DO NOT SHAKE ME!!"+alpha_, Toast.LENGTH_LONG);
                toast.show();
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Started!", Toast.LENGTH_LONG).show();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI , new Handler());

        acellast = SensorManager.GRAVITY_EARTH;
        acelval = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        alpha_ = intent.getIntExtra("alpha" ,12 );
        Log.i("hithere:)))" , String.valueOf(alpha_));

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }



    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Service Destroyed!", Toast.LENGTH_LONG).show();
        sm.unregisterListener(sensorEventListener);
        super.onDestroy();

    }

}

