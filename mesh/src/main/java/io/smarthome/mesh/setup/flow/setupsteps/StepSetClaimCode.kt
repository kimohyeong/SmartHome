package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.throwOnErrorOrAbsent


class StepSetClaimCode : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        // if claim code is null, we don't intend to claim
        if (ctxs.ble.targetDevice.isClaimedLD.value == true || ctxs.cloud.shouldBeClaimed == false) {
            return
        }

        val targetXceiver = ctxs.ble.targetDevice.transceiverLD.value!!
        targetXceiver.sendSetClaimCode(ctxs.cloud.claimCode!!).throwOnErrorOrAbsent()
    }

}