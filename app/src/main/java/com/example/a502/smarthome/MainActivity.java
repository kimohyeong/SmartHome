package com.example.a502.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloudSDK;

public class MainActivity extends AppCompatActivity {

    public ImageView []room;
    public Boolean isAdd4, isAdd5;
    ArrayList<Device>[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        setting();
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
        d.room[0] = 0;
        d.type=0;
        d.name="myled";
        d.state="on";
        devices[0].add(d);
        d1.room[0] = 2;
        d1.type=1;
        d1.name="myblind";
        d1.state="on";
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
}
