package io.particle.android.sdk.retrofitLib;

import com.google.gson.annotations.SerializedName;

public class PArgonInfo {


    @SerializedName("return_value")
    public final int return_value;

    public PArgonInfo(int return_value)
    {
        this.return_value = return_value;
    }

    public int getReturnValue() {
        return return_value;
    }
}
