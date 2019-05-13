package io.particle.android.sdk;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import io.particle.android.sdk.cloudDB.DBhelper;
import io.particle.android.sdk.retrofitLib.PArgonInfo;
import io.particle.android.sdk.retrofitLib.PNetworkInfo;
import io.particle.android.sdk.retrofitLib.ParticleRetrofitApi;
import io.particle.sdk.app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddDeviceActivity extends AppCompatActivity {
    private DBhelper helper = new DBhelper(this);
    private static int dbID = new Random().nextInt(999999);    //  database ID를 위해 하나씩 증가함

    private final String BASE_URL = "https://api.particle.io/v1/";
    private Retrofit retrofit;
    private boolean isAfterMeshSetup=false;
    private ParticleRetrofitApi particleApi;
    private int returnValue;

    ProgressDialog progressDialog;

    EditText newDeivceNameEdit;
    RadioGroup  deviceRadioGroup;
    Spinner spinner;

    // Device data
    Device newDevice;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // ParticleRetrofitApi API 인터페이스 생성
        particleApi = retrofit.create(ParticleRetrofitApi.class);

        //Intent intent = new Intent(this, SplashActivity.class);
        //startActivity(intent);

        newDevice = new Device();
        newDevice.setDeviceType(-1);

        newDeivceNameEdit = findViewById(R.id.newDeviceNameEdit);
        spinner = findViewById(R.id.myspinner);
        deviceRadioGroup = findViewById(R.id.deviceRadioGroup);
        deviceRadioGroup.setOnCheckedChangeListener(radioGroupListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newDevice.setDeviceRoomNum(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int color = Color.parseColor("#46547f");
        newDeivceNameEdit.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isAfterMeshSetup=true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("log1","onResume");

        if(isAfterMeshSetup)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            //int deviceCount= helper.getDevicesCount();
            int deviceCount= 4;

            //요청
            Call<List<PNetworkInfo>> call = particleApi.getDeviceCount("c71a8d2cb891e50a9a5f0a18921f366abef86271");


            //요청 수행
            call.enqueue(new Callback<List<PNetworkInfo>>()
            {
                @Override
                public void onResponse(Call<List<PNetworkInfo>> call, Response<List<PNetworkInfo>> response) {
                    List<PNetworkInfo> info = response.body();

                    Log.e("log1", info.get(1).device_count);

                    progressDialog.dismiss();
                    //if(Integer.parseInt(info.get(1).device_count) <= deviceCount)
                  //  {
                  //      finish();
                  //  }


                }

                @Override

                public void onFailure(Call<List<PNetworkInfo>> call, Throwable t) {
                    Log.e("log1","실패!");
                }

            });

        }


    }


    // Setting Listener
    RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.deviceRadioGroup:
                    if(checkedId == R.id.r_light) {
                        newDevice.setDeviceType(0);
                    } else if(checkedId == R.id.r_rgb_light) {
                        newDevice.setDeviceType(1);
                    } else if(checkedId == R.id.r_blind) {
                        newDevice.setDeviceType(2);
                    } else if(checkedId == R.id.r_fan) {
                        newDevice.setDeviceType(3);
                    } else if(checkedId == R.id.r_thermometer) {
                        newDevice.setDeviceType(4);
                    }
                    break;
            }
        }
    };

    // DB에 추가
    public void onClickOK(View v)
    {
        String name = newDeivceNameEdit.getText().toString();
        if(name.isEmpty() || newDevice.getDeviceType() == -1){
            Toast.makeText(this,"모든 설정을 완료 해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        String msg = newDevice.getDeviceRoomNum()+"/"+name+"/"+newDevice.getDeviceType();
        Call<PArgonInfo> call = particleApi.callInitDevice(msg,"c71a8d2cb891e50a9a5f0a18921f366abef86271");

        call.enqueue(new Callback<PArgonInfo>()
        {
            @Override
            public void onResponse(Call<PArgonInfo> call, Response<PArgonInfo> response) {
                PArgonInfo argonInfo = response.body();
                returnValue=argonInfo.getReturnValue();
                Log.e("log1-adddevice",returnValue+"");

                if(returnValue > 0)
                {
                    // id는 고유값 위해 계속 ++
                    newDevice.setDeviceId(dbID++);
                    newDevice.setDeviceState("0");  //처음 상태
                    newDevice.setDeviceName(name);
                    newDevice.setDeviceDetailState("");
                    newDevice.setDeviceCustomRoomNum1(-1);
                    newDevice.setDeviceCustomRoomNum2(-1);
                    helper.addDevice(newDevice);

                    helper.printAllDevices();

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "기기를 추가 하지 못했습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override

            public void onFailure(Call<PArgonInfo> call, Throwable t) {
                Log.e("log1-argon","실패!");
                returnValue=-1;

            }

        });

        finish();
    }


    public void onClickClear(View v){
        newDeivceNameEdit.setText("");
    }


    public void onClickBack(View v){
        finish();
    }
}