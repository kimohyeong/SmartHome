package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate

class StepShowCellularConnectingToDeviceCloudUi(
    private val flowUi: FlowUiDelegate
) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cellular.connectingToCloudUiShown) {
            return
        }
        ctxs.cellular.connectingToCloudUiShown = true
        flowUi.showConnectingToDeviceCloudCellularUi()

    }

}
