package com.example.a502.smarthome;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        init();
        getData();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        List<String> nameList = Arrays.asList("fan", "blind", "air", "led");
        List<String> infoList = Arrays.asList("1", "2", "3", "4");
        List<String> stateList = Arrays.asList("4", "3", "2", "1");
        List<Drawable> drawableList = Arrays.asList(getResources().getDrawable(R.drawable.fan),getResources().getDrawable(R.drawable.fan),getResources().getDrawable(R.drawable.fan),getResources().getDrawable(R.drawable.fan));
        for(int i=0; i<4; i++) {
            RecyclerData data = new RecyclerData();
            data.setDeviceImgDrawable(drawableList.get(i));
            data.setDeviceInfo(infoList.get(i));
            data.setDeviceState(stateList.get(i));
            data.setDeviceName(nameList.get(i));
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }
}
