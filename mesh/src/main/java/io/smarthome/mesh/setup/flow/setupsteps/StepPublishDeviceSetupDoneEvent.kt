package io.smarthome.mesh.setup.flow.setupsteps

import androidx.annotation.WorkerThread
import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.android.sdk.cloud.ParticleCloudSDK
import io.smarthome.android.sdk.cloud.ParticleEventVisibility
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import java.util.concurrent.TimeUnit


class StepPublishDeviceSetupDoneEvent(val cloud: ParticleCloud) : MeshSetupStep() {

    @WorkerThread
    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        ParticleCloudSDK.getCloud().publishEvent(
            "mesh-setup-session-complete",
            null,
            ParticleEventVisibility.PRIVATE,
            TimeUnit.HOURS.toSeconds(1).toInt()
        )
    }

}