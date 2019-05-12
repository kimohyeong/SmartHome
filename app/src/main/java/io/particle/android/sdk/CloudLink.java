package io.particle.android.sdk;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class CloudLink {

    //방번호/디바이스이름/state
    public int setDevice(int roomNum, int idx, String state){
        String command;

        if(idx==-1)
        {
            command = roomNum+"/"+"ALL"+"/"+"0";
        }
        else
        {
            String deviceName = SmartHomeMainActivity.devices[roomNum].get(idx).getDeviceName();
            command = roomNum+"/"+deviceName+"/"+state;
        }
        

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                try {
                    String cmd = command;

                    List<String> msg = new ArrayList<>();
                    msg.add(cmd);

                    int resultCode = SmartHomeMainActivity.meshGateway.callFunction("setDevice", msg);
                    Log.e("log1",command + resultCode);

                }  catch (ParticleDevice.FunctionDoesNotExistException e) {
                    e.printStackTrace();
                    return -1;
                }
                return 1;
            }
            @Override
            public void onSuccess(@NonNull Object value) {


            }
            @Override
            public void onFailure(@NonNull ParticleCloudException e) {

            }
        });

        return 1;
    }
}
