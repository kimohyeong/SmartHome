package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.MeshSetupFlowException
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate


class StepSetNewDeviceName(
    private val flowUi: FlowUiDelegate,
    private val cloud: ParticleCloud
) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cloud.isTargetDeviceNamedLD.value == true) {
            return
        }

        val nameToAssign = ctxs.cloud.targetDeviceNameToAssignLD
            .nonNull(scopes)
            .runBlockOnUiThreadAndAwaitUpdate(scopes) {
                flowUi.showNameDeviceUi()
            }

        if (nameToAssign == null) {
            throw MeshSetupFlowException("Error ensuring target device is named")
        }

        try {
            flowUi.showGlobalProgressSpinner(true)

            val targetDeviceId = ctxs.ble.targetDevice.deviceId!!
            val joiner = cloud.getDevice(targetDeviceId)
            joiner.name = nameToAssign
            ctxs.cloud.updateIsTargetDeviceNamed(true)

        } catch (ex: Exception) {
            throw MeshSetupFlowException("Unable to rename device", ex)

        } finally {
            flowUi.showGlobalProgressSpinner(true)
        }
    }
}