package com.example.a502.smarthome;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    EditText editText;
    ListView listView;
    addViewAdapter mAdapter;
    ArrayList<Device>[] devices;
    ArrayList<Device> device;
    static boolean[] addNum ;

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

        addNum =  new boolean[100];
        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.addList0);

        device= new ArrayList<Device>();
        for(int i=0; i<4; i++){
            for(int j=0; j<devices[i].size(); j++){
                if(j==0)
                    devices[i].get(j).setIsFirst(true);
                else
                    devices[i].get(j).setIsFirst(false);
                device.add((devices[i].get(j)));
            }
        }
        //adapter
        mAdapter = new addViewAdapter(this, device);
        listView.setAdapter(mAdapter);
    }
    public void onClickOK(View v){
        ArrayList<Device> addDevice = new ArrayList<Device>();

        //check된 device addDevice에저장
        for(int i=0; i<device.size(); i++){
            if(addNum[i]){
                addDevice.add(device.get(i));
            }
        }

        //finish & transger addDevice
        Intent intent = new Intent();
        intent.putExtra("AddDevice",addDevice);
        intent.putExtra("AddName",editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

    }
    public void onClickClear(View v){
        editText.setText("");
    }


    public void onClickBack(View v){
        finish();
    }
}
