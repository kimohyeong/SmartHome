package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.throwOnErrorOrAbsent

class StepSetSetupDone : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        val xceiver = ctxs.requireTargetXceiver()
        xceiver.sendSetDeviceSetupDone(true).throwOnErrorOrAbsent()
    }

}