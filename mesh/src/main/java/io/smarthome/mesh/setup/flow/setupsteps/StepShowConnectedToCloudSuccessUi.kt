package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.R
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import io.smarthome.mesh.setup.flow.toUserFacingName
import kotlinx.coroutines.delay


class StepShowConnectedToCloudSuccessUi(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cloud.connectedToCloudCongratsUiShown) {
            return
        }

        ctxs.cloud.connectedToCloudCongratsUiShown = true

        val template = flowUi.getString(R.string.p_congrats_claimed)
        val msg = template.format(ctxs.ble.targetDevice.deviceType?.toUserFacingName())
        flowUi.showSingleTaskCongratsScreen(msg)
        delay(1900)
    }

}