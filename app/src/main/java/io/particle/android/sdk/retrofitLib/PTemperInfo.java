package io.particle.android.sdk.retrofitLib;

import com.google.gson.annotations.SerializedName;

public class PTemperInfo {
    @SerializedName("result")
    public final String result;

    public PTemperInfo(String result)
    {
        this.result = result;
    }

    public String getTemperInfo() {return this.result;}

}
