package io.smarthome.mesh.setup.flow.setupsteps

import androidx.annotation.WorkerThread
import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.android.sdk.cloud.PricingImpactAction
import io.smarthome.android.sdk.cloud.PricingImpactNetworkType
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.FatalFlowException
import io.smarthome.mesh.setup.flow.Gen3ConnectivityType
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.NetworkSetupType
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import io.smarthome.mesh.setup.flow.modules.meshsetup.MeshNetworkToJoin


class StepShowPricingImpact(
    private val flowUi: FlowUiDelegate,
    private val cloud: ParticleCloud
) : MeshSetupStep() {

    @WorkerThread
    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.pricingImpactConfirmedLD.value == true) {
            return
        }

        flowUi.showGlobalProgressSpinner(true)
        try {
            ensurePricingImpactRetrieved(ctxs)
        } finally {
            flowUi.showGlobalProgressSpinner(false)
        }

        ctxs.pricingImpactConfirmedLD.nonNull(scopes).runBlockOnUiThreadAndAwaitUpdate(scopes) {
            flowUi.showPricingImpactScreen()
        }
    }


    private fun ensurePricingImpactRetrieved(ctxs: SetupContexts) {
        val action = when (ctxs.device.networkSetupTypeLD.value) {
            NetworkSetupType.AS_GATEWAY -> PricingImpactAction.CREATE_NETWORK
            NetworkSetupType.STANDALONE -> PricingImpactAction.ADD_USER_DEVICE
            NetworkSetupType.NODE_JOINER -> throw FatalFlowException(
                "Should not be showing billing for joiners!"
            )
            null -> PricingImpactAction.ADD_NETWORK_DEVICE
        }

        val connType = ctxs.ble.targetDevice.connectivityType
        val networkType = if (connType == Gen3ConnectivityType.CELLULAR) {
            PricingImpactNetworkType.CELLULAR
        } else {
            PricingImpactNetworkType.WIFI
        }

        val selectedNetwork = ctxs.mesh.targetDeviceMeshNetworkToJoinLD.value
        val networkId = when (selectedNetwork) {
            is MeshNetworkToJoin.SelectedNetwork -> selectedNetwork.networkToJoin.networkId
            is MeshNetworkToJoin.CreateNewNetwork,
            null -> null
        }

        ctxs.cloud.pricingImpact = cloud.getPricingImpact(
            action = action,
            deviceId = ctxs.ble.targetDevice.deviceId,
            networkId = networkId,
            networkType = networkType,
            iccid = ctxs.ble.targetDevice.iccid
        )
    }
}