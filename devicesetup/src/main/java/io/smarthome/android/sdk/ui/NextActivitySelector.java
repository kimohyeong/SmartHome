package io.smarthome.android.sdk.ui;

import android.content.Context;
import android.content.Intent;

import io.smarthome.android.sdk.accountsetup.CreateAccountActivity;
import io.smarthome.android.sdk.accountsetup.LoginActivity;
import io.smarthome.android.sdk.cloud.ParticleCloud;
import io.smarthome.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.smarthome.android.sdk.devicesetup.SetupCompleteIntentBuilder;
import io.smarthome.android.sdk.devicesetup.SetupResult;
import io.smarthome.android.sdk.persistance.SensitiveDataStorage;
import io.smarthome.android.sdk.utils.Preconditions;
import io.smarthome.android.sdk.utils.TLog;

import static io.smarthome.android.sdk.utils.Py.any;
import static io.smarthome.android.sdk.utils.Py.truthy;

/**
 * Selects the next Activity in the workflow, up to the "GetReady" screen or main UI.
 */
public class NextActivitySelector {

    private static final TLog log = TLog.get(NextActivitySelector.class);


    public static Intent getNextActivityIntent(Context ctx,
                                               ParticleCloud particleCloud,
                                               SensitiveDataStorage credStorage,
                                               SetupResult setupResult) {
        NextActivitySelector selector = new NextActivitySelector(particleCloud, credStorage,
                ParticleDeviceSetupLibrary.getInstance().getSetupCompleteIntentBuilder());

        return selector.buildIntentForNextActivity(ctx, setupResult);
    }


    private final ParticleCloud cloud;
    private final SensitiveDataStorage credStorage;
    private final SetupCompleteIntentBuilder setupCompleteIntentBuilder;

    private NextActivitySelector(ParticleCloud cloud,
                                 SensitiveDataStorage credStorage,
                                 SetupCompleteIntentBuilder setupCompleteIntentBuilder) {
        Preconditions.checkNotNull(setupCompleteIntentBuilder, "SetupCompleteIntentBuilder instance is null");

        this.cloud = cloud;
        this.credStorage = credStorage;
        this.setupCompleteIntentBuilder = setupCompleteIntentBuilder;
    }

    Intent buildIntentForNextActivity(Context ctx, SetupResult result) {
        if (!hasUserBeenLoggedInBefore() && !BaseActivity.setupOnly) {
            log.d("User has not been logged in before");
            return new Intent(ctx, CreateAccountActivity.class);
        }

        if (!isOAuthTokenPresent() && !BaseActivity.setupOnly) {
            log.d("No auth token present");
            return new Intent(ctx, LoginActivity.class);
        }

        log.d("Building setup complete activity...");
        Intent successActivity = setupCompleteIntentBuilder.buildIntent(ctx, result);

        log.d("Returning setup complete activity");
        return successActivity;
    }

    boolean hasUserBeenLoggedInBefore() {
        return any(credStorage.getUser(), credStorage.getToken());
    }

    boolean isOAuthTokenPresent() {
        return truthy(cloud.getAccessToken());
    }

}
