package io.smarthome.android.sdk.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.smarthome.android.sdk.cloud.ParticleCloudSDK;
import io.smarthome.android.sdk.cloud.SDKGlobals;
import io.smarthome.android.sdk.utils.ui.Ui;
import io.smarthome.sdk.app.R;


public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        String version = "?.?.?";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Ui.setText(this, R.id.version, "v" + version);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

       // Ui.findView(this, R.id.set_up_button).setOnClickListener(view -> {
            Intent intent = NextActivitySelector.getNextActivityIntent(
                    //view.getContext(),
                    this,
                    ParticleCloudSDK.getCloud(),
                    SDKGlobals.getSensitiveDataStorage(),
                    null);
            startActivity(intent);
            finish();
       // });


    }

}
