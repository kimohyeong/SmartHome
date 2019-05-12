package io.particle.android.sdk;

import com.google.gson.annotations.SerializedName;

public class PNetworkInfo {

 //   public final String gateway_count;
    @SerializedName("device_count")
    public final String device_count;
 //   public final String id;
    @SerializedName("name")
    public final String name;

 //   public final String type;
  //  public final String pan_id;

   // public final String xpan_id;
   //// public final String channel;
   // public final String last_heard;

   // public final String notes;

    public PNetworkInfo(String device_count, String name)
    {
        this.device_count = device_count;
        this.name=name;
    }

}
