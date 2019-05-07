package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.firmwareprotos.ctrl.Config.DeviceMode
import io.smarthome.firmwareprotos.ctrl.Config.Feature
import io.smarthome.mesh.setup.flow.*
import io.smarthome.mesh.setup.flow.context.SetupContexts
import kotlinx.coroutines.delay


class StepEnsureCorrectEthernetFeatureStatus : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (!ctxs.device.shouldDetectEthernet || ctxs.device.shouldDetectEthernet) {
            return
        }

        val target = ctxs.ble.targetDevice.transceiverLD.value!!
        target.sendSetFeature(Feature.ETHERNET_DETECTION, true).throwOnErrorOrAbsent()
        ctxs.device.isDetectEthernetSent = true
        target.sendStartupMode(DeviceMode.LISTENING_MODE).throwOnErrorOrAbsent()
        target.sendReset().throwOnErrorOrAbsent()

        target.disconnect()
        delay(4000)

        throw MeshSetupFlowException(
            "Resetting device to enable ethernet detection!",
            severity = ExceptionType.EXPECTED_FLOW
        )
    }

}