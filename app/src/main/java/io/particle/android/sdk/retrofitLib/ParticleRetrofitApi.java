package io.particle.android.sdk.retrofitLib;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ParticleRetrofitApi {

    @GET("networks")
    Call<List<PNetworkInfo>> getDeviceCount(@Query("access_token") String access_token);

    @GET("devices/e00fce68eff6c37be64c2915/temperData")
    Call<PTemperInfo> getTemperData(@Query("access_token") String access_token);

    @FormUrlEncoded
    @POST("devices/e00fce68eff6c37be64c2915/setDevice")
    Call<PArgonInfo> callSetDevice(@Field("arg") String arg, @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("devices/e00fce68eff6c37be64c2915/initDevice")
    Call<PArgonInfo> callInitDevice(@Field("arg") String arg, @Field("access_token") String access_token);






}
