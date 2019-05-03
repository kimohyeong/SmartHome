package io.smarthome.android.sdk.accountsetup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.smarthome.android.sdk.cloud.ParticleCloud;
import io.smarthome.android.sdk.cloud.SDKGlobals;
import io.smarthome.android.sdk.cloud.exceptions.ParticleCloudException;
import io.smarthome.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.smarthome.android.sdk.devicesetup.R;
import io.smarthome.android.sdk.devicesetup.R2;
import io.smarthome.android.sdk.di.ApModule;
import io.smarthome.android.sdk.ui.BaseActivity;
import io.smarthome.android.sdk.ui.NextActivitySelector;
import io.smarthome.android.sdk.utils.Async;
import io.smarthome.android.sdk.utils.SEGAnalytics;
import io.smarthome.android.sdk.utils.TLog;
import io.smarthome.android.sdk.utils.ui.ParticleUi;
import io.smarthome.android.sdk.utils.ui.Ui;

import static io.smarthome.android.sdk.utils.Py.truthy;


public class TwoFactorActivity extends BaseActivity {
    public static final String EMAIL_EXTRA = "email";
    public static final String PASSWORD_EXTRA = "password";
    public static final String MFA_EXTRA = "mfa_token";

    private static final TLog log = TLog.get(TwoFactorActivity.class);

    /**
     * Keep track of the login task to ensure we can cancel it if requested, ensure against
     * duplicate requests, etc.
     */
    private Async.AsyncApiWorker<ParticleCloud, Void> loginTask = null;

    public static Intent buildIntent(Context context, String email, String password, String mfa) {
        Intent i = new Intent(context, TwoFactorActivity.class);
        if (truthy(email)) {
            i.putExtra(EMAIL_EXTRA, email);
        }
        if (truthy(password)) {
            i.putExtra(PASSWORD_EXTRA, password);
        }
        if (truthy(mfa)) {
            i.putExtra(MFA_EXTRA, mfa);
        }
        return i;
    }

    @Inject
    protected ParticleCloud sparkCloud;

    @BindView(R2.id.verificationCode)
    protected EditText verificationCode;

    @OnClick(R2.id.recover_auth)
    public void onRecover() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.recovery_link)));
        startActivity(intent);
    }

    @OnClick(R2.id.action_verify)
    public void onVerify() {
        if (loginTask != null) {
            log.wtf("Login being attempted again even though the button isn't enabled?!");
            return;
        }

        final String code = verificationCode.getText().toString();

        if (TextUtils.isEmpty(code)) {
            verificationCode.setError(getString(R.string.error_field_required));
            verificationCode.requestFocus();
        } else {
            Intent callingIntent = getIntent();
            login(callingIntent.getStringExtra(EMAIL_EXTRA), callingIntent.getStringExtra(PASSWORD_EXTRA),
                    callingIntent.getStringExtra(MFA_EXTRA), code);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor);
        //dependency injection
        ParticleDeviceSetupLibrary
                .getInstance()
                .getApplicationComponent()
                .activityComponentBuilder()
                .apModule(new ApModule())
                .build()
                .inject(this);
        // Bind views, onclick listeners, etc.
        ButterKnife.bind(this);
        SEGAnalytics.screen("Auth: Two Factor Screen");

        ParticleUi.enableBrandLogoInverseVisibilityAgainstSoftKeyboard(this);

        Ui.setTextFromHtml(this, R.id.recover_auth, R.string.recover_link_text);
    }

    /**
     * Attempts to sign in the account with two-factor authentication.
     */
    private void login(String email, String password, String mfaToken, String otp) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        ParticleUi.showParticleButtonProgress(this, R.id.action_verify, true);
        loginTask = Async.executeAsync(sparkCloud, new Async.ApiWork<ParticleCloud, Void>() {
            @Override
            public Void callApi(@NonNull ParticleCloud sparkCloud) throws ParticleCloudException {
                sparkCloud.logIn(email, password, mfaToken, otp);
                return null;
            }

            @Override
            public void onTaskFinished() {
                loginTask = null;
            }

            @Override
            public void onSuccess(@NonNull Void result) {
                SEGAnalytics.identify(email);
                SEGAnalytics.track("Auth: Two Factor success");
                log.d("Logged in...");

                if (!isFinishing()) {
                    startActivity(NextActivitySelector.getNextActivityIntent(
                            TwoFactorActivity.this,
                            sparkCloud,
                            SDKGlobals.getSensitiveDataStorage(),
                            null));
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException error) {
                log.d("onFailed(): " + error.getMessage());
                SEGAnalytics.track("Auth: Two Factor failure");

                if (!isFinishing()) {
                    ParticleUi.showParticleButtonProgress(TwoFactorActivity.this, R.id.action_verify, false);
                    verificationCode.setError(error.getBestMessage());
                    verificationCode.requestFocus();
                }
            }
        });
    }
}