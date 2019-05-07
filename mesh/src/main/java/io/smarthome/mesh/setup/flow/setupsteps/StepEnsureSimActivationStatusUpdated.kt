package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.android.sdk.cloud.models.ParticleSimStatus
import io.smarthome.mesh.setup.flow.MeshSetupFlowException
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts


class StepEnsureSimActivationStatusUpdated(private val cloud: ParticleCloud) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cellular.isSimActivatedLD.value == true) {
            return
        }

        // is SIM activated
        val statusAndMsg = cloud.checkSim(ctxs.cellular.targetDeviceIccid.value!!)

        val isActive = when (statusAndMsg.first) {
            ParticleSimStatus.READY_TO_ACTIVATE -> false

            ParticleSimStatus.ACTIVATED_FREE,
            ParticleSimStatus.ACTIVATED -> true

            ParticleSimStatus.NOT_FOUND,
            ParticleSimStatus.NOT_OWNED_BY_USER,
            ParticleSimStatus.ERROR -> throw MeshSetupFlowException(statusAndMsg.second)
        }

        ctxs.cellular.updateIsSimActivated(isActive)
    }

}
