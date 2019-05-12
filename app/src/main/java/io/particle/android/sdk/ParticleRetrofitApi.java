package io.particle.android.sdk;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ParticleRetrofitApi {

    @GET("networks")
    Call<List<PNetworkInfo>> getDeviceCount(@Query("access_token") String access_token);



}
