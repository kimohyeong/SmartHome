package com.example.a502.smarthome;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    EditText editText;
    ListView[] listView;
    addViewAdapter[] mAdapter;
    ArrayList<Device>[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();
        devices = (ArrayList<Device>[])intent.getSerializableExtra("DEVICE");
        setting();
    }

    public void setting(){
        Log.d("super","setting");///////////
        editText = findViewById(R.id.editText);
        listView = new ListView[4];
        listView[0] = findViewById(R.id.addList0);
        listView[1] = findViewById(R.id.addList1);
        listView[2] = findViewById(R.id.addList2);
        listView[3] = findViewById(R.id.addList3);

        //adapter
        mAdapter = new addViewAdapter[4];
        for(int i=0; i<4; i++){
            mAdapter[i] = new addViewAdapter(this, devices[i]);
            listView[i].setAdapter(mAdapter[i]);
        }
    }
    public void onClickClear(View v){
        editText.setText("");
    }
}
