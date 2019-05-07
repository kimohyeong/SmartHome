package io.particle.android.sdk;

import android.graphics.drawable.Drawable;

public class RecyclerData {
    private String deviceName;
    private String deviceInfo;
    private String deviceState;
    private Drawable deviceImgDrawable;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String _deviceName) {
        this.deviceName = _deviceName;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String _deviceInfo) {
        this.deviceInfo = _deviceInfo;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(String _deviceState) {
        this.deviceState = _deviceState;
    }

    public Drawable getDeviceImgDrawable() {
        return deviceImgDrawable;
    }

    public void setDeviceImgDrawable(Drawable _deviceImgDrawable) {
        this.deviceImgDrawable = _deviceImgDrawable;
    }
}
