package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate


class StepShowShouldConnectToDeviceCloudConfirmation(
    private val flowUi: FlowUiDelegate
) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        val confirmationLD = ctxs.cloud.shouldConnectToDeviceCloudConfirmed
        if (confirmationLD.value == true) {
            return
        }

        confirmationLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.showInternetConnectedConnectToDeviceCloudIntroUi()
        }
    }

}