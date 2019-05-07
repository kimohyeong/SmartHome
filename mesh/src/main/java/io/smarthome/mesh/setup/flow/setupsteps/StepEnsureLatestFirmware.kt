package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.ota.FirmwareUpdateManager
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import mu.KotlinLogging


class StepEnsureLatestFirmware(
    private val flowUi: FlowUiDelegate,
    private val firmwareUpdateManager: FirmwareUpdateManager
) : MeshSetupStep() {

    private val log = KotlinLogging.logger {}

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.ble.targetDevice.hasLatestFirmware) {
            log.info { "Already checked device for latest firmware; skipping" }
            flowUi.showGlobalProgressSpinner(false)
            return
        }

        val xceiver = ctxs.ble.targetDevice.transceiverLD.value!!
        val deviceType = ctxs.ble.targetDevice.deviceType!!

        val needsUpdate = firmwareUpdateManager.needsUpdate(xceiver, deviceType)
        if (!needsUpdate) {
            log.debug { "No firmware update needed!" }
            ctxs.ble.targetDevice.hasLatestFirmware = true
            return
        }

        ctxs.device.userConsentedToFirmwareUpdateLD
            .nonNull(scopes)
            .runBlockOnUiThreadAndAwaitUpdate(scopes) {
                flowUi.showBleOtaIntroUi()
            }

        ctxs.device.updateBleOtaProgress(0)
        // FIXME: change state to "updating OTA via BLE"
        flowUi.showBleOtaUi()

        firmwareUpdateManager.startUpdateIfNecessary(xceiver, deviceType) {
            ctxs.device.updateBleOtaProgress(it)
        }

        // FIXME: this probably shouldn't live in the flow, but in the UI
        flowUi.showGlobalProgressSpinner(true)
        ctxs.device.firmwareUpdateCount++
    }

}