package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.ExpectedFlowException
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate


class StepAwaitSetupStandAloneOrWithNetwork(
    val cloud: ParticleCloud,
    val flowUi: FlowUiDelegate
) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.device.networkSetupTypeLD.value != null) {
            return
        }

        ctxs.device.networkSetupTypeLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.getNetworkSetupType()
        }

        // reset flow again!
        val networkSetupType = ctxs.device.networkSetupTypeLD.value
        throw ExpectedFlowException("Network setup type selected: $networkSetupType")
    }

}
