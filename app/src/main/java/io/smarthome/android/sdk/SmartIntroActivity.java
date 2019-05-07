package io.smarthome.android.sdk;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import io.smarthome.sdk.app.R;

public class SmartIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_intro);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SmartIntroActivity.this, SmartHomeMainActivity.class);
                startActivity(intent);
                finish();
            }
        },1300);
    }
}