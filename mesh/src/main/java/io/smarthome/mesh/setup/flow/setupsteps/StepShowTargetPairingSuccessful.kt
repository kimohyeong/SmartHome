package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import kotlinx.coroutines.delay


class StepShowTargetPairingSuccessful(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        val deviceName = ctxs.requireTargetXceiver().bleBroadcastName
        val shouldWaitBeforeAdvancingFlow = flowUi.onTargetPairingSuccessful(deviceName)
        if (shouldWaitBeforeAdvancingFlow) {
            delay(2000)
        }
    }

}