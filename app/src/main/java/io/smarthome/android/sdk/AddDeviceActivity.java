package io.smarthome.android.sdk;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Random;

import io.smarthome.android.sdk.cloudDB.DBhelper;
import io.smarthome.sdk.app.R;

public class AddDeviceActivity extends AppCompatActivity {
    private DBhelper helper = new DBhelper(this);
    private static int dbID = new Random().nextInt(999999);    //  database ID를 위해 하나씩 증가함

    EditText newDeivceNameEdit;
    RadioGroup roomRadioGroup, deviceRadioGroup;

    // Device data
    Device newDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        newDevice = new Device();

        newDeivceNameEdit = findViewById(R.id.newDeviceNameEdit);
        roomRadioGroup = findViewById(R.id.roomRadioGroup);
        deviceRadioGroup = findViewById(R.id.deviceRadioGroup);
        roomRadioGroup.setOnCheckedChangeListener(radioGroupListener);
        deviceRadioGroup.setOnCheckedChangeListener(radioGroupListener);

        ActionBar bar = getSupportActionBar();
        bar.hide();
    }

    // Setting Listener
    RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.roomRadioGroup:
                        if(checkedId == R.id.livingRadioBtn) {
                            newDevice.setDeviceRoomNum(0);
                        } else if(checkedId == R.id.bathRadioBtn) {
                            newDevice.setDeviceRoomNum(1);
                        } else if(checkedId == R.id.bedRadioBtn) {
                            newDevice.setDeviceRoomNum(2);
                        } else if(checkedId == R.id.studyRadioBtn) {
                            newDevice.setDeviceRoomNum(3);
                        }
                    break;
                case R.id.deviceRadioGroup:
                    if(checkedId == R.id.r_light) {
                        newDevice.setDeviceType(0);
                    } else if(checkedId == R.id.r_blind) {
                        newDevice.setDeviceType(1);
                    } else if(checkedId == R.id.r_fan) {
                        newDevice.setDeviceType(2);
                    }
                    break;
            }
        }
    };

    // DB에 추가
    public void onClickOK(View v)
    {
        // id는 고유값 위해 계속 ++
        newDevice.setDeviceId(dbID++);
        newDevice.setDeviceState("false");  //처음 상태
        newDevice.setDeviceName(newDeivceNameEdit.getText().toString());
        helper.addDevice(newDevice);

        helper.printAllDevices();

        finish();
    }

}
