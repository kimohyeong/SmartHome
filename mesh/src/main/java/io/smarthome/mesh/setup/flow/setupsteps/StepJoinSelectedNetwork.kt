package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.firmwareprotos.ctrl.mesh.Mesh.NetworkInfo
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.meshsetup.MeshNetworkToJoin
import io.smarthome.mesh.setup.flow.throwOnErrorOrAbsent
import kotlinx.coroutines.delay


class StepJoinSelectedNetwork(private val cloud: ParticleCloud) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.mesh.targetJoinedSuccessfully) {
            return
        }

        val joiner = ctxs.requireTargetXceiver()
        val commish = ctxs.requireCommissionerXceiver()

        // no need to send auth msg here; we already authenticated when the password was collected
        commish.sendStartCommissioner().throwOnErrorOrAbsent()
        ctxs.mesh.updateCommissionerStarted(true)

        val toJoinWrapper =  ctxs.mesh.targetDeviceMeshNetworkToJoinLD.value!!
        val networkToJoin: NetworkInfo = when (toJoinWrapper) {
            is MeshNetworkToJoin.SelectedNetwork -> toJoinWrapper.networkToJoin
            is MeshNetworkToJoin.CreateNewNetwork -> throw IllegalStateException()
        }
        val prepJoinerData = joiner.sendPrepareJoiner(networkToJoin).throwOnErrorOrAbsent()
        commish.sendAddJoiner(prepJoinerData.eui64, prepJoinerData.password).throwOnErrorOrAbsent()

        // value here recommended by Sergey
        delay(10000)

        joiner.sendJoinNetwork().throwOnErrorOrAbsent()
        ctxs.mesh.updateTargetJoinedMeshNetwork(true)

        cloud.addDeviceToMeshNetwork(
            ctxs.ble.targetDevice.deviceId!!,
            networkToJoin.networkId
        )

        ctxs.mesh.targetJoinedSuccessfully = true
        // let the success UI show for a moment
        delay(2000)
    }
}
