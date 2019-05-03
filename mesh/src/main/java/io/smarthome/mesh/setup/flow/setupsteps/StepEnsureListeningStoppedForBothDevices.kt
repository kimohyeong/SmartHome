package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts


class StepEnsureListeningStoppedForBothDevices : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        ctxs.ble.targetDevice.transceiverLD.value?.sendStopListeningMode()
        ctxs.ble.commissioner.transceiverLD.value?.sendStopListeningMode()
    }

}