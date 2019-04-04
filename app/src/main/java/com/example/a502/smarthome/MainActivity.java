package com.example.a502.smarthome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {

    String email="kdhdud15@naver.com";
    String password="k726843oy";
    ImageView []room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        particleInit();
        setting();
    }

    public void particleInit()
    {

        ParticleCloudSDK.init(this);
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            private List<ParticleDevice> devices;

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                particleCloud.logIn(email, password);

                devices=particleCloud.getDevices();

                for (ParticleDevice device : devices) {
                    //리스트 저장
                    if (device.getName().equals("myDevice")) {
                        //doSomethingWithMyDevice(device);
                        break;
                    }
                }

                return -1;

            }

            @Override
            public void onSuccess(@NonNull Object value) {
                Log.e("log1", "login success");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Log.d("log1", e.getBestMessage());
            }
        });


    }
    public void setting(){

        room = new ImageView[6];
        room[0] = findViewById(R.id.room0);
        room[1] = findViewById(R.id.room1);
        room[2] = findViewById(R.id.room2);
        room[3] = findViewById(R.id.room3);
        room[4] = findViewById(R.id.room4);
        room[5] = findViewById(R.id.room5);
    }

    public void onClickRoom(View v){
        int roomNum = -1;
        for(int i=0; i<6; i++){
            if(v == room[i]){
                roomNum = i;
                break;
            }
        }

        Intent intent ;
        switch (roomNum){
            case 0:{
                break;
            }
            case 1:{
                break;
            }
            case 2:{
                break;
            }
            case 3:{
                break;
            }
            case 4:{
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                break;
            }
            case 5:{
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
