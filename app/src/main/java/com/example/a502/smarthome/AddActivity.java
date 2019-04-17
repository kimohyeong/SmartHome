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
import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    EditText editText;
    ListView[] listView;
    addViewAdapter[] mAdapter;
    //ArrayList<Device>[] devices;
    int roomNum;
    static int[][] addNum = new int[4][100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Log.e("super","dddd");
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();
        roomNum = intent.getIntExtra("ROOM_NUM",0);
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
            mAdapter[i] = new addViewAdapter(this, MainActivity.devices[i], i);
            listView[i].setAdapter(mAdapter[i]);
        }
    }
    public void onClickOK(View v){
        ArrayList<Device> addDevice = new ArrayList<Device>();

        //check된 device addDevice에저장
        for(int i=0; i<4; i++){
            for(int j=0; j<5; j++){
                if(addNum[i][j] == 1){
                    addDevice.add(MainActivity.devices[i].get(j));
                }
            }
        }

        //finish & transger addDevice
        Intent intent = new Intent();
        MainActivity.devices[roomNum] = addDevice;
       // intent.putExtra("AddDevice",addDevice);
        intent.putExtra("AddName",editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

    }
    public void onClickClear(View v){
        editText.setText("");
    }

    //checkbox check listener
    public static class OnCheckedChange implements CompoundButton.OnCheckedChangeListener{
        int roomNum, idx;

        public void setDevice(int roomNum, int idx){
            this.roomNum = roomNum;
            this.idx = idx;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("super","FF : "+roomNum+"/"+idx);
            if(isChecked)
                addNum[roomNum][idx] = 1;
            else
                addNum[roomNum][idx] = -1;
        }
    }

    public void onClickBack(View v){
        finish();
    }
}
