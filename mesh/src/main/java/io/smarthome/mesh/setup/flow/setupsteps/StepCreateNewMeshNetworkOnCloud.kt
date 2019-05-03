package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.android.sdk.cloud.ParticleNetworkType
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.common.truthy
import io.smarthome.mesh.setup.flow.Gen3ConnectivityType
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts


class StepCreateNewMeshNetworkOnCloud(private val cloud: ParticleCloud) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.mesh.newNetworkIdLD.value.truthy()) {
            return
        }

        val networkType = when (ctxs.ble.targetDevice.connectivityType!!) {
            Gen3ConnectivityType.WIFI -> ParticleNetworkType.MICRO_WIFI
            Gen3ConnectivityType.CELLULAR -> ParticleNetworkType.MICRO_CELLULAR
            Gen3ConnectivityType.MESH_ONLY -> ParticleNetworkType.MICRO_WIFI
        }

        val networkResponse = cloud.registerMeshNetwork(
            ctxs.ble.targetDevice.deviceId!!,
            networkType,
            ctxs.mesh.newNetworkNameLD.value!!
        )

        // set the network ID and wait for it to update
        ctxs.mesh.newNetworkIdLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            ctxs.mesh.updateNewNetworkIdLD(networkResponse.id)
        }
    }

}