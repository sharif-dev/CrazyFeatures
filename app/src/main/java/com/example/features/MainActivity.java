package com.example.features;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    SwitchCompat switch1;
    SwitchCompat switch2;
    TextView shakesensivity;
    SeekBar shakeseekbar;
    TextView locksensivity;
    SeekBar lockscreenbar;
    private int progress_;
    private int _progress;
    private  int num = 23;
    

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);



        switch1 = findViewById(R.id.shakedetect);
        switch2 = findViewById(R.id.Lockscreen);
        shakesensivity = findViewById(R.id.alpha);
        shakeseekbar=findViewById(R.id.shakesense);
        lockscreenbar = findViewById(R.id.lockangel);
        locksensivity=findViewById(R.id.beta);

        shakeseekbar.setVisibility(View.GONE);
        shakesensivity.setVisibility(View.GONE);

        lockscreenbar.setVisibility(View.GONE);
        locksensivity.setVisibility(View.GONE);

        SharedPreferences sharedPrefs = getSharedPreferences("com.example.features", MODE_PRIVATE);
        switch1.setChecked(sharedPrefs.getBoolean("tag", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    final Intent first = new Intent(MainActivity.this , ShakeService.class);
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.features", MODE_PRIVATE).edit();
                    editor.putBoolean("tag", true);
                    editor.apply();
                    shakeseekbar.setVisibility(View.VISIBLE);
                    shakesensivity.setVisibility(View.VISIBLE);
                    shakesensivity.setText("ShakeDegree: "+ 12);
                    shakeseekbar.setProgress(12);

                    shakeseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            shakesensivity.setText("ShakeDegree: "+progress);
                            progress_ = shakeseekbar.getProgress();
                            first.putExtra("alpha"  , progress_);
                            startService(first);
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                    startService(first);


                }
                else
                {
                    stopService(new Intent(MainActivity.this , ShakeService.class));
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.features", MODE_PRIVATE).edit();
                    editor.putBoolean("tag", false);
                    editor.apply();
                    shakeseekbar.setVisibility(View.GONE);
                    shakesensivity.setVisibility(View.GONE);

                }

            }
        });



        //////////////    THIRD FEATURES /////////////////////////
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);



        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean active = devicePolicyManager.isAdminActive(compName);

                if (isChecked)
                {


                    if (active)
                    {



                        final Intent intent = new Intent(MainActivity.this , LockService.class);

                        lockscreenbar.setVisibility(View.VISIBLE);
                        locksensivity.setVisibility(View.VISIBLE);
                        locksensivity.setText("Degree: "+ 25);
                        lockscreenbar.setProgress(25);
                        lockscreenbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                locksensivity.setText("Degree: "+progress);
                                _progress = lockscreenbar.getProgress();
                                intent.putExtra("beta"  , _progress);
                                startService(intent);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        startService(intent);



                    }

                    else
                    {

                        //// Avtice device permision
                        Toast.makeText(getApplicationContext(), "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
                        startActivityForResult(intent, RESULT_ENABLE);

                        if (active)
                        {
                            final Intent intent2 = new Intent(MainActivity.this , LockService.class);
                            lockscreenbar.setVisibility(View.VISIBLE);
                            locksensivity.setVisibility(View.VISIBLE);
                            locksensivity.setText("Degree: "+ 25);
                            lockscreenbar.setProgress(25);
                            lockscreenbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    locksensivity.setText("Degree: "+progress);
                                    _progress = lockscreenbar.getProgress();
                                    intent2.putExtra("beta"  , _progress);
                                    startService(intent2);
                                }
                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                }
                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                }
                            });
                        }
                        else {
                            Log.i("stop service" , String.valueOf(active)) ;
                            stopService(new Intent(MainActivity.this , LockService.class));
                            switch2.setChecked(false);

                        }

                    }

                }
                else
                {
                    Log.i("stop service" , String.valueOf(active)) ;
                    stopService(new Intent(MainActivity.this , LockService.class));
                    devicePolicyManager.removeActiveAdmin(compName);
                    lockscreenbar.setVisibility(View.GONE);
                    locksensivity.setVisibility(View.GONE);

                }


            }
        });

    }


}
