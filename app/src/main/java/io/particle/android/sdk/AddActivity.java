package io.particle.android.sdk;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.particle.sdk.app.R;

public class AddActivity extends AppCompatActivity {

    EditText editText;
    ListView listView;
    addViewAdapter mAdapter;
    //ArrayList<Device>[] devices;
    ArrayList<Device> device;
    static boolean[] addNum ;
    int roomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();
        roomNum = intent.getIntExtra("ROOM_NUM",0);
        setting();
    }

    public void setting(){
        Log.d("super","setting");///////////


        addNum =  new boolean[100];
        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.addList0);
        //int color = Color.parseColor("#ff0000");
        editText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        device= new ArrayList<Device>();
        for(int i=0; i<4; i++){
            for(int j = 0; j< SmartHomeMainActivity.devices[i].size(); j++){
                if(j==0)
                    SmartHomeMainActivity.devices[i].get(j).setIsFirst(true);
                else
                    SmartHomeMainActivity.devices[i].get(j).setIsFirst(false);
                device.add((SmartHomeMainActivity.devices[i].get(j)));
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
        SmartHomeMainActivity.devices[roomNum] = addDevice;
        //intent.putExtra("AddDevice",addDevice);
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
