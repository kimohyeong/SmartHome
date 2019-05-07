package io.smarthome.mesh.setup.flow.setupsteps

import androidx.annotation.WorkerThread
import io.smarthome.firmwareprotos.ctrl.wifi.WifiNew.Security.NO_SECURITY
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.common.truthy
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate


class StepCollectSelectedWifiNetworkPassword(
    private val flowUi: FlowUiDelegate
) : MeshSetupStep() {

    @WorkerThread
    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.wifi.targetWifiNetworkPasswordLD.value.truthy()
            || ctxs.wifi.targetWifiNetworkLD.value?.security == NO_SECURITY
        ) {
            return
        }

        ctxs.wifi.targetWifiNetworkPasswordLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.showSetWifiPasswordUi()
        }

        flowUi.showGlobalProgressSpinner(true)
    }

}