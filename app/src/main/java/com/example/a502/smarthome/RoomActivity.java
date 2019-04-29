package com.example.a502.smarthome;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    private RecyclerAdapter adapter;
    private int roomNum = 0;
    private String roomName;
    private int roomImg;

    //act num
    TextView actnum;
    TextView inactnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();

        roomNum = intent.getIntExtra("ROOM_NUM",0);
        roomName = intent.getStringExtra("NAME");
        roomImg = intent.getIntExtra("IMG",0);
        init();
        getData();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        //위layout
        TextView txtRoom = findViewById(R.id.txt_room);
        ImageView imgRoom = findViewById(R.id.img_room);
        actnum = (TextView)findViewById(R.id.actNum);
        inactnum = (TextView)findViewById(R.id.inactNum);

        txtRoom.setText(roomName);
        imgRoom.setImageResource(roomImg);

        int an=0,ian=0;
        for(int i=0; i<MainActivity.devices[roomNum].size(); i++){
            if(MainActivity.devices[roomNum].get(i).getDeviceState().substring(0,2).equals("ON"))
                an++;
            else
                ian++;
        }
        actnum.setText(String.valueOf(an));
        inactnum.setText(String.valueOf(ian));
    }

    private void getData() {
        if (MainActivity.devices[roomNum] == null) {
            Log.e("getData","devices is null");
            return;
        }

        // 0 - led, 1 - blind, 2 - fan
        List<Drawable> drawableList = Arrays.asList(getResources().getDrawable(R.drawable.light),getResources().getDrawable(R.drawable.blind),getResources().getDrawable(R.drawable.fan));
        for(int i=0; i<MainActivity.devices[roomNum].size(); i++) {
            Device data = MainActivity.devices[roomNum].get(i);
            data.setDeviceName(data.getDeviceName());
            data.setDeviceImgDrawable(drawableList.get(data.getDeviceType()));
            data.setDeviceType(data.getDeviceType());
            data.setDeviceState(data.getDeviceState());
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }

    public void onClickBack(View v){
        finish();
    }
}
