package io.particle.android.sdk.cloudDB;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import io.particle.android.sdk.Device;
import io.particle.android.sdk.SmartHomeMainActivity;
import io.particle.android.sdk.retrofitLib.PArgonInfo;
import io.particle.android.sdk.retrofitLib.PTemperInfo;
import io.particle.android.sdk.retrofitLib.ParticleRetrofitApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudLink {

    private final String BASE_URL = "https://api.particle.io/v1/";
    private Retrofit retrofit;
    private ParticleRetrofitApi particleApi;
    private int returnValue;
    private String returnStrValue;
    private Context context;

    public CloudLink(){}
    public CloudLink(Context context) {
        this.context = context;
    }

    //방번호/디바이스이름/state
    public int setDevice(String msg){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // ParticleRetrofitApi API 인터페이스 생성
        particleApi = retrofit.create(ParticleRetrofitApi.class);

        Log.e("log1-argon", msg);
        //요청
        Call<PArgonInfo> call = particleApi.callSetDevice(msg,"c71a8d2cb891e50a9a5f0a18921f366abef86271");

        //요청 수행
        call.enqueue(new Callback<PArgonInfo>()
        {
            @Override
            public void onResponse(Call<PArgonInfo> call, Response<PArgonInfo> response) {
                PArgonInfo argonInfo = response.body();

                Log.e("log1-argon", "성공");
                Log.e("log1-argon", argonInfo.getReturnValue()+"");

                returnValue=argonInfo.getReturnValue();
            }

            @Override
            public void onFailure(Call<PArgonInfo> call, Throwable t) {
                Log.e("log1-argon","실패!");
                returnValue=-1;
            }
        });
        return 1;
    }



    public int initDevice(String msg){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // ParticleRetrofitApi API 인터페이스 생성
        particleApi = retrofit.create(ParticleRetrofitApi.class);
        Log.e("log1-argon", msg);

        Call<PArgonInfo> call = particleApi.callInitDevice(msg,"c71a8d2cb891e50a9a5f0a18921f366abef86271");

        call.enqueue(new Callback<PArgonInfo>()
        {
            @Override
            public void onResponse(Call<PArgonInfo> call, Response<PArgonInfo> response) {
                PArgonInfo argonInfo = response.body();
                returnValue=argonInfo.getReturnValue();
                Log.e("log1-adddevice",returnValue+"");
            }

            @Override
            public void onFailure(Call<PArgonInfo> call, Throwable t) {
                Log.e("log1-argon","실패!");
                returnValue=-1;
            }
        });
        return 1;
    }
    public void getTemperData() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // ParticleRetrofitApi API 인터페이스 생성
        particleApi = retrofit.create(ParticleRetrofitApi.class);

        Call<PTemperInfo> call = particleApi.getTemperData("c71a8d2cb891e50a9a5f0a18921f366abef86271");

        call.enqueue(new Callback<PTemperInfo>()
        {
            @Override
            public void onResponse(Call<PTemperInfo> call, Response<PTemperInfo> response) {
                if(response.isSuccessful()) {
                    PTemperInfo argonInfo = response.body();
                    returnStrValue = argonInfo.getTemperInfo();
                    Log.e("update temper: ",returnStrValue);

                    getTemperDataCallback();
                    Log.e("log1-adddevice",": success");
                }
            }

            @Override
            public void onFailure(Call<PTemperInfo> call, Throwable t) {
                Log.e("log1-argon","실패!");
            }
        });
        return;
    }

    void getTemperDataCallback() {
        DBhelper helper = new DBhelper(this.context);
        if(helper.getDevicesCount() == 0) {
            return;
        }
        ArrayList<Device>[] devices = SmartHomeMainActivity.devices;
        Device device;
        // 0 ~  5까지 방 순회
        for(int i=0;i<6;i++) {
            // 만약 방에 아무 디바이스도 없다면 continue
            if(devices[i].size() == 0) continue;

            // 방에 있는 device들 순회
            for(int j=0; j<devices[i].size(); j++) {
                device = devices[i].get(j);
                // type이 temper면 update
                if(device.getDeviceType() == 4) {
                    device.setDeviceDetailState(returnStrValue);
                    helper.updateDevice(device);
                }
            }
        }
    }
}

