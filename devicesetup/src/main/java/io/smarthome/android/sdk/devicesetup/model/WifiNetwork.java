package io.smarthome.android.sdk.devicesetup.model;

import io.smarthome.android.sdk.utils.SSID;


public interface WifiNetwork {

    SSID getSsid();

    boolean isSecured();
}
