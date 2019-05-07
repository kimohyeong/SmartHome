package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.Result
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.connection.ResultCode
import io.smarthome.mesh.setup.flow.MeshSetupFlowException
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import io.smarthome.mesh.setup.ui.DialogSpec.StringDialogSpec


class StepEnsureSelectedWifiNetworkJoined(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.wifi.targetWifiNetworkJoinedLD.value == true) {
            return
        }

        val xceiver = ctxs.requireTargetXceiver()

        flowUi.showGlobalProgressSpinner(true)

        val joinNewNetworkResponse = xceiver.sendJoinNewNetwork(
            ctxs.wifi.targetWifiNetworkLD.value!!,
            ctxs.wifi.targetWifiNetworkPasswordLD.value!!
        )

        val genericError = MeshSetupFlowException(
            "Could not join to Wi-Fi network due to an unknown error"
        )

        val dialogHack = flowUi.dialogTool

        when (joinNewNetworkResponse) {
            is Result.Present -> ctxs.wifi.updateTargetWifiNetworkJoined(true)
            is Result.Absent -> throw genericError
            is Result.Error -> {
                if (joinNewNetworkResponse.error == ResultCode.NOT_FOUND) {

                    dialogHack.clearDialogResult()


                    dialogHack.dialogResultLD
                        .nonNull(scopes)
                        .runBlockOnUiThreadAndAwaitUpdate(scopes) {
                            dialogHack.newDialogRequest(
                                // FIXME: i18n!
                                StringDialogSpec(
                                    "Could not connect to Wi-Fi.  Please try entering your password again."
                                )
                            )
                        }
                    dialogHack.clearDialogResult()
                    ctxs.wifi.updateTargetWifiNetworkPassword(null)
                    ctxs.wifi.connectingToCloudUiShown = false
                    throw MeshSetupFlowException("Error connecting to Wi-Fi (bad password?)")

                } else {
                    throw genericError
                }
            }
        }
    }

}