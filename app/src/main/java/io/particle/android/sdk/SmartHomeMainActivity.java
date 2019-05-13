package io.particle.android.sdk;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloudDB.DBhelper;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;
import io.particle.sdk.app.R;

public class SmartHomeMainActivity extends AppCompatActivity {

    //view
    public ImageView []room;
    public Button reBtn4, reBtn5;
    public TextView txtV4, txtV5;

    public Boolean isAdd4=false, isAdd5=false;
    public String name4, name5;
    public static ArrayList<Device>[] devices;
    public static ParticleDevice meshGateway;

    DBhelper helper;

    //sp
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smarthome_main);

        ParticleDeviceSetupLibrary.init(this.getApplicationContext());
        helper = new DBhelper(this);
        devices = new ArrayList[6];
        for(int i=0; i<6; i++){
            devices[i] = new ArrayList<Device>();
        }

        ActionBar bar = getSupportActionBar();
        bar.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("smart main: ","onResume"+helper.getDevicesCount()+"");
        initComponents();

        if(helper.getDevicesCount() != 0) {
            devices = helper.getAllDevices();

            // set custom room UI
            if(devices[4].size() > 0) {
                room[4].setImageResource(R.drawable.person);
                isAdd4 = true;
                reBtn4.setEnabled(isAdd4);
                reBtn4.setVisibility(View.VISIBLE);
//                name4 = intent.getStringExtra("AddName");
                txtV4.setText("");
            } else {
                room[4].setImageResource(R.drawable.plus);
                isAdd4 = false;
                reBtn4.setEnabled(isAdd4);
                reBtn4.setVisibility(View.GONE);
//                name4 = intent.getStringExtra("AddName");
                txtV4.setText("");
            }
            if (devices[5].size() > 0) {
                room[5].setImageResource(R.drawable.person);
                isAdd5 = true;
                reBtn5.setEnabled(isAdd5);
                reBtn5.setVisibility(View.VISIBLE);
//                name5 = intent.getStringExtra("AddName");
                txtV5.setText("");
            } else {
                room[5].setImageResource(R.drawable.plus);
                isAdd5 = false;
                reBtn5.setEnabled(isAdd5);
                reBtn5.setVisibility(View.GONE);
//                name5 = intent.getStringExtra("AddName");
                txtV5.setText("");
            }
        }
    }

    public void initComponents(){
        room = new ImageView[6];
        room[0] = findViewById(R.id.room0);
        room[1] = findViewById(R.id.room1);
        room[2] = findViewById(R.id.room2);
        room[3] = findViewById(R.id.room3);
        room[4] = findViewById(R.id.room4);
        room[5] = findViewById(R.id.room5);

        reBtn4 = findViewById(R.id.resetBtn1);
        reBtn5 = findViewById(R.id.resetBtn2);
        txtV4 = findViewById(R.id.txtv4);
        txtV5 = findViewById(R.id.txtv5);
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
            intent.putExtra("ROOM_NUM",roomNum);
            startActivity(intent);
            return;
        }
        if(roomNum == 5 && !isAdd5) {
            intent = new Intent(this, AddActivity.class);
            intent.putExtra("ROOM_NUM",roomNum);
            startActivity(intent);
            return;
        }

        //start roomActivity
        String []roomName  = {"Living Room","BathRoom","Bedroom","Study Room",name4,name5};
        int [] roomImg = {R.drawable.livingroom, R.drawable.bathroom, R.drawable.bedroom, R.drawable.studyroom,R.drawable.person,R.drawable.person};
        intent = new Intent(this, RoomActivity.class);
        intent.putExtra("NAME",roomName[roomNum]);
        //intent.putExtra("DEVICE",devices[roomNum]);
        intent.putExtra("ROOM_NUM",roomNum);
        intent.putExtra("IMG",roomImg[roomNum]);
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

    ///----------- room reset butoon ------------------///
    public void onClickRe(View v){
        int roomNum=0;
        switch (v.getId()){
            case R.id.resetBtn1:{
                roomNum = 4;
                break;
            }
            case R.id.resetBtn2:{
                roomNum = 5;
                break;
            }
        }

        // check dialog
        final Dialog dialog = new Dialog(SmartHomeMainActivity.this);
        dialog.setContentView(R.layout.dialog_check);
        Button btnOK = (Button)dialog.findViewById(R.id.check_OKbtn);
        Button btnCancel = (Button)dialog.findViewById(R.id.check_CancelBtn);

        final int finalRoomNum = roomNum;
        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reset(finalRoomNum);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void reset(int roomNum){
        if(roomNum == 4){
            isAdd4 = false;
            reBtn4.setEnabled(isAdd4);
            reBtn4.setVisibility(View.GONE);
            name4 = "";
            txtV4.setText(name4);
            editor.putString("Name4",name4);
            editor.putBoolean("IsAdd4",isAdd4);
            room[4].setImageResource(R.drawable.plus);
        }
        // room5 reset //
        else if(roomNum == 5){
            isAdd5 = false;
            reBtn5.setEnabled(isAdd5);
            reBtn5.setVisibility(View.GONE);
            name5 = "";
            txtV5.setText(name5);
            editor.putString("Name5",name5);
            editor.putBoolean("IsAdd5",isAdd5);
            room[5].setImageResource(R.drawable.plus);
        }
        editor.commit();
    }

    public void plusDevice(View v) {
        Intent intent = new Intent(this, AddDeviceActivity.class);
        startActivity(intent);
    }
}

