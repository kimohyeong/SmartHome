package io.particle.android.sdk.cloudDB;

import android.util.Log;

import io.particle.android.sdk.retrofitLib.PArgonInfo;
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
}
