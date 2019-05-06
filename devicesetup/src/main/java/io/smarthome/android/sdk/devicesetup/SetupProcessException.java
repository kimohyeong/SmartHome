package io.smarthome.android.sdk.devicesetup;


import io.smarthome.android.sdk.devicesetup.setupsteps.SetupStep;

public class SetupProcessException extends Exception {

    public final SetupStep failedStep;

    public SetupProcessException(String msg, SetupStep failedStep) {
        super(msg);
        this.failedStep = failedStep;
    }
}
