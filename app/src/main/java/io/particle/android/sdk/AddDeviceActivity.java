package io.particle.android.sdk;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import io.particle.sdk.app.R;

public class AddDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        ActionBar bar = getSupportActionBar();
        bar.hide();
    }

    public void onClickOK(View v)
    {
    }

}
