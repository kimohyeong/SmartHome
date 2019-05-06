package io.smarthome.android.sdk.cloud.exceptions;

import java.util.List;

import io.smarthome.android.sdk.cloud.ParticleDevice;
import retrofit.RetrofitError;

public class PartialDeviceListResultException extends Exception {

    public final List<ParticleDevice> devices;

    public PartialDeviceListResultException(List<ParticleDevice> devices, Exception cause) {
        super(cause);
        this.devices = devices;
    }

    public PartialDeviceListResultException(List<ParticleDevice> devices, RetrofitError error) {
        super(error);
        this.devices = devices;
    }

    public PartialDeviceListResultException(List<ParticleDevice> devices) {
        super("Undefined error while fetching devices");
        this.devices = devices;
    }
}
