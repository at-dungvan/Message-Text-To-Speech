package com.example.dung.messagetospeech.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.lib.CustomTTSService;
import com.example.dung.messagetospeech.services.BackgroundServices;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();
        setContentView(R.layout.activity_main);

        CustomTTSService.initTTS(getApplication().getApplicationContext());
        startService(new Intent(this, BackgroundServices.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Method request permission for app
     */
    private void checkPermission() {
        // Permission read contact and sms
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS}, 1);
            }
        }
    }
}
