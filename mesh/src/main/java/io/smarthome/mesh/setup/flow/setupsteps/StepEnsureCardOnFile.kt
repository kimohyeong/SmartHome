package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.mesh.R
import io.smarthome.mesh.common.android.livedata.nonNull
import io.smarthome.mesh.common.android.livedata.runBlockOnUiThreadAndAwaitUpdate
import io.smarthome.mesh.setup.flow.*
import io.smarthome.mesh.setup.flow.ExceptionType.ERROR_FATAL
import io.smarthome.mesh.setup.flow.context.SetupContexts
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import io.smarthome.mesh.setup.ui.DialogResult
import io.smarthome.mesh.setup.ui.DialogSpec.ResDialogSpec
import mu.KotlinLogging


class StepEnsureCardOnFile(
    private val flowUi: FlowUiDelegate,
    private val cloud: ParticleCloud
) : MeshSetupStep() {


    private val log = KotlinLogging.logger {}


    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cloud.paymentCardOnFile) {
            return
        }

        flowUi.showGlobalProgressSpinner(true)

        val cardResponse = cloud.getPaymentCard()
        ctxs.cloud.paymentCardOnFile = cardResponse.card?.last4 != null
        if (ctxs.cloud.paymentCardOnFile) {
            return
        }

        val dialogResult = flowUi.dialogTool.dialogResultLD
            .nonNull(scopes)
            .runBlockOnUiThreadAndAwaitUpdate(scopes) {
                flowUi.dialogTool.newDialogRequest(
                    ResDialogSpec(
                        R.string.p_mesh_billing_please_go_to_your_browser,
                        android.R.string.ok,
                        R.string.p_mesh_action_exit_setup
                    )
                )
            }

        log.info { "Result for leave network confirmation dialog: $dialogResult" }

        flowUi.dialogTool.clearDialogResult()

        val err = when (dialogResult) {
            DialogResult.POSITIVE -> ExpectedFlowException(
                "Restarting flow after user confirmed payment card"
            )
            DialogResult.NEGATIVE -> MeshSetupFlowException(
                "User choosing not to enter payment card; exiting setup",
                severity = ERROR_FATAL
            )
            null -> MeshSetupFlowException("Unknown error when asking user to enter payment card")
        }
        throw err
    }

}