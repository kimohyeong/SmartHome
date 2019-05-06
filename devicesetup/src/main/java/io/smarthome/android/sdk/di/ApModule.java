package io.smarthome.android.sdk.di;

import android.content.Context;
import androidx.annotation.RestrictTo;

import dagger.Module;
import dagger.Provides;
import io.smarthome.android.sdk.devicesetup.ApConnector;
import io.smarthome.android.sdk.devicesetup.commands.CommandClientFactory;
import io.smarthome.android.sdk.devicesetup.setupsteps.SetupStepsFactory;
import io.smarthome.android.sdk.devicesetup.ui.DiscoverProcessWorker;
import io.smarthome.android.sdk.utils.SoftAPConfigRemover;
import io.smarthome.android.sdk.utils.WifiFacade;

@Module
@RestrictTo({RestrictTo.Scope.LIBRARY})
public class ApModule {

    @Provides
    protected SoftAPConfigRemover providesSoftApConfigRemover(Context context, WifiFacade wifiFacade) {
        return new SoftAPConfigRemover(context, wifiFacade);
    }

    @Provides
    protected WifiFacade providesWifiFacade(Context context) {
        return WifiFacade.get(context);
    }

    @Provides
    protected DiscoverProcessWorker providesDiscoverProcessWorker() {
        return new DiscoverProcessWorker();
    }

    @Provides
    protected CommandClientFactory providesCommandClientFactory() {
        return new CommandClientFactory();
    }

    @Provides
    protected SetupStepsFactory providesSetupStepsFactory() {
        return new SetupStepsFactory();
    }

    @Provides
    protected ApConnector providesApConnector(Context context, SoftAPConfigRemover softAPConfigRemover,
                                              WifiFacade wifiFacade) {
        return new ApConnector(context, softAPConfigRemover, wifiFacade);
    }
}
