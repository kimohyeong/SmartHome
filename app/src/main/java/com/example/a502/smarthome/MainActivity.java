package com.example.a502.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {

    public ImageView []room;
    public Boolean isAdd4, isAdd5;
    ArrayList<Device>[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBar bar = getSupportActionBar();
        //bar.hide();

        //particleInit();
        //setting();
    }

    public void particleInit()
    {

        ParticleCloudSDK.init(this);
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            private List<ParticleDevice> particleDevices;

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {


                try {
                    JSONObject obj = new JSONObject(loadJSONFromAsset());
                    Log.e("log1",obj.optString("cloudEmail"));
                    Log.e("log1",obj.optString("cloudPassword"));
                    //particleCloud.logIn(obj.optString("cloudEmail"), obj.optString("cloudPassword"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("log1","login success");

                particleDevices=particleCloud.getDevices();

                for (ParticleDevice device : particleDevices) {
                    //리스트 저장

                    Device d=new Device();

                    int roomnum=0;
                    //클라우드에서 가져오기
                    //roomnum=Integer.parseInt(device.getStringVariable("subRoomNum"));
                    d.setDeviceRoom(0,9);
                    d.setDeviceState("");
                    d.setDeviceName("");
                    d.setDeviceType(1);

                    devices[roomnum].add(d);
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
        //view
        room = new ImageView[6];
        room[0] = findViewById(R.id.room0);
        room[1] = findViewById(R.id.room1);
        room[2] = findViewById(R.id.room2);
        room[3] = findViewById(R.id.room3);
        room[4] = findViewById(R.id.room4);
        room[5] = findViewById(R.id.room5);

        ///is add personalize room4,room5
        SharedPreferences pref = getSharedPreferences("IsAdd",MODE_PRIVATE);
        isAdd4 = pref.getBoolean("IsAdd4",false);
        isAdd5 = pref.getBoolean("IsAdd5",false);
        /*
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("IsAdd4",isAdd4);
        editor.putBoolean("IsAdd5",isAdd5);
        editor.commit();
        */

        if(isAdd4 == false)
            room[4].setImageResource(R.drawable.plus);
        else
            room[4].setImageResource(R.drawable.person);
        if(isAdd5 == false)
            room[5].setImageResource(R.drawable.plus);
        else
            room[5].setImageResource(R.drawable.person);

        //device
        devices = new ArrayList[4];
        for(int i=0; i<4; i++){
            devices[i] = new ArrayList<Device>();
        }
        Device d = new Device();
        Device d1 = new Device();
        d.setDeviceRoom(0, 0);
        d.setDeviceType(0);
        d.setDeviceName("MyLED");
        d.setDeviceState("on");
        devices[0].add(d);

        d.setDeviceRoom(0, 2);
        d.setDeviceType(1);
        d.setDeviceName("MyBlind");
        d.setDeviceState("on");
        devices[2].add(d1);
    }

    public void onClickRoom(View v){
        int roomNum = -1;
        Intent intent;
        for(int i=0; i<6; i++){
            if(v == room[i]){
                roomNum = i;
                break;
            }
        }

        //start addActivity
        if(roomNum == 4 && !isAdd4){
            intent = new Intent(this, AddActivity.class);
            intent.putExtra("DEVICE",devices);
            startActivity(intent);
            return;
        }
        if(roomNum == 5 && !isAdd5) {
            intent = new Intent(this, AddActivity.class);
            intent.putExtra("DEVICE",devices);
            startActivity(intent);
            return;
        }

        //start roomActivity
        intent = new Intent(this, RoomActivity.class);
        intent.putExtra("DEVICE",devices[roomNum]);
        startActivity(intent);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("infoConfig.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

}
