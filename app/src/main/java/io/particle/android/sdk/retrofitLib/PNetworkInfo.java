package io.particle.android.sdk.retrofitLib;

import com.google.gson.annotations.SerializedName;

public class PNetworkInfo {

    @SerializedName("device_count")
    public final String device_count;

    @SerializedName("name")
    public final String name;

    public PNetworkInfo(String device_count, String name)
    {
        this.device_count = device_count;
        this.name=name;
    }

}
