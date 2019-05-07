package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import mu.KotlinLogging


class StepCollectUserWifiNetworkSelection(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    private val log = KotlinLogging.logger {}

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.wifi.targetWifiNetworkLD.value != null) {
            return
        }

        val network = ctxs.wifi.targetWifiNetworkLD
            .nonNull(scopes)
            .runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.showScanForWifiNetworksUi()
        }

        log.info { "Selected network: $network" }
    }

}