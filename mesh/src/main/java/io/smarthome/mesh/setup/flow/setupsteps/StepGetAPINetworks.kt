package io.smarthome.mesh.setup.flow.setupsteps

import androidx.annotation.WorkerThread
import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts


class StepGetAPINetworks(private val cloud: ParticleCloud) : MeshSetupStep() {

    @WorkerThread
    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        val networks = cloud.getNetworks()
        ctxs.cloud.apiNetworks = networks
    }

}