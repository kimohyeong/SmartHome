package io.smarthome.android.sdk.di;

import androidx.annotation.RestrictTo;

import dagger.Subcomponent;
import io.smarthome.android.sdk.accountsetup.LoginActivity;
import io.smarthome.android.sdk.accountsetup.PasswordResetActivity;
import io.smarthome.android.sdk.accountsetup.TwoFactorActivity;
import io.smarthome.android.sdk.devicesetup.ui.ConnectToApFragment;
import io.smarthome.android.sdk.devicesetup.ui.ConnectingActivity;
import io.smarthome.android.sdk.devicesetup.ui.ConnectingProcessWorkerTask;
import io.smarthome.android.sdk.devicesetup.ui.DiscoverDeviceActivity;
import io.smarthome.android.sdk.devicesetup.ui.GetReadyActivity;
import io.smarthome.android.sdk.devicesetup.ui.ManualNetworkEntryActivity;
import io.smarthome.android.sdk.devicesetup.ui.PasswordEntryActivity;
import io.smarthome.android.sdk.devicesetup.ui.SelectNetworkActivity;
import io.smarthome.android.sdk.devicesetup.ui.SuccessActivity;

@PerActivity
@Subcomponent(modules = {ApModule.class})
@RestrictTo({RestrictTo.Scope.LIBRARY})
public interface ActivityInjectorComponent {
    void inject(GetReadyActivity activity);

    void inject(LoginActivity loginActivity);

    void inject(PasswordResetActivity passwordResetActivity);

    void inject(SuccessActivity successActivity);

    void inject(DiscoverDeviceActivity discoverDeviceActivity);

    void inject(ConnectingActivity connectingActivity);

    void inject(PasswordEntryActivity passwordEntryActivity);

    void inject(ManualNetworkEntryActivity manualNetworkEntryActivity);

    void inject(SelectNetworkActivity selectNetworkActivity);

    void inject(ConnectToApFragment connectToApFragment);

    void inject(ConnectingProcessWorkerTask connectingProcessWorkerTask);

    void inject(TwoFactorActivity twoFactorActivity);

    @Subcomponent.Builder
    interface Builder {
        Builder apModule(ApModule module);

        ActivityInjectorComponent build();
    }

}
