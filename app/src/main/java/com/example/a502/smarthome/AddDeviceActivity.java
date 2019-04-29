package com.example.a502.smarthome;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import io.particle.android.sdk.accountsetup.CreateAccountActivity;
import io.particle.android.sdk.accountsetup.LoginActivity;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.mesh.ui.setup.MeshSetupActivity;

import java.util.List;

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
        //Particle앱 존재 검사
        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if(mApps.get(i).activityInfo.packageName.startsWith("io.particle.android.app")){
                    isExist = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            isExist = false;
        }

        //존재하면 앱 실행
        if(isExist)
        {
            ComponentName compName=new ComponentName("io.particle.android.app","io.particle.android.sdk.ui.IntroActivity");
            Intent intent =new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(compName);

            // Intent intent = getPackageManager().getLaunchIntentForPackage("io.particle.android.app");
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        else
        {
            //ParticleDeviceSetupLibrary.startDeviceSetup(this, MainActivity.class);
            Log.e("log1", "adddeviceactiyi");
            Intent intent=new Intent(v.getContext(), CreateAccountActivity.class);
            startActivity(intent);
        }
        finish();
    }

}
