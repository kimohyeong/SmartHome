package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate


class StepGetNewMeshNetworkPassword(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.mesh.newNetworkPasswordLD.value != null) {
            return
        }

        ctxs.mesh.newNetworkPasswordLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.getNewMeshNetworkPassword()
        }
    }

}