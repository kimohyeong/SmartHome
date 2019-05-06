package io.smarthome.android.sdk.cloud;

/**
 * Created by Julius.
 */

public interface SimpleParticleEventHandler {
    void onEvent(String eventName, ParticleEvent particleEvent);
}
