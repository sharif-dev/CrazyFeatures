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
import android.os.Parcelable;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    SwitchCompat switch1;
    SwitchCompat switch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       switch1 = findViewById(R.id.shakedetect);
       switch2 = findViewById(R.id.Lockscreen);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);

        SharedPreferences sharedPrefs = getSharedPreferences("com.example.features", MODE_PRIVATE);
        switch1.setChecked(sharedPrefs.getBoolean("tag", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    startService(new Intent(MainActivity.this , ShakeService.class));
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.features", MODE_PRIVATE).edit();
                    editor.putBoolean("tag", true);
                    editor.apply();
                }
                else
                {
                    stopService(new Intent(MainActivity.this , ShakeService.class));
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.features", MODE_PRIVATE).edit();
                    editor.putBoolean("tag", false);
                    editor.apply();
                }

            }
        });



        //////////////    THIRD FEATURES /////////////////////////

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean active = devicePolicyManager.isAdminActive(compName);



                if (isChecked)
                {
                    if (active)
                    {
                        Intent intent = new Intent(MainActivity.this , LockService.class);
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
                            Intent intent2 = new Intent(MainActivity.this, LockService.class);
                            startService(intent2);
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

                }







            }
        });

    }
}
