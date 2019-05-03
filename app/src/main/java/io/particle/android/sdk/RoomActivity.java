package io.particle.android.sdk;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import io.particle.sdk.app.R;

public class RoomActivity extends AppCompatActivity {
    private RecyclerAdapter adapter;
    private int roomNum = 0;
    private String roomName;
    private int roomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();

        // 방에 있는 device들 가져오기
        roomNum = intent.getIntExtra("ROOM_NUM",0);
        //devices = (ArrayList<Device>)intent.getSerializableExtra("DEVICE");
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
        txtRoom.setText(roomName);
        ImageView imgRoom = findViewById(R.id.img_room);
        imgRoom.setImageResource(roomImg);
    }

    private void getData() {
        if (SmartHomeMainActivity.devices[roomNum] == null) {
            Log.e("getData","devices is null");
            return;
        }

        // 0 - led, 1 - blind, 2 - fan
        List<Drawable> drawableList = Arrays.asList(getResources().getDrawable(R.drawable.light),getResources().getDrawable(R.drawable.blind),getResources().getDrawable(R.drawable.fan));
        for(int i = 0; i< SmartHomeMainActivity.devices[roomNum].size(); i++) {
            Device data = SmartHomeMainActivity.devices[roomNum].get(i);
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
