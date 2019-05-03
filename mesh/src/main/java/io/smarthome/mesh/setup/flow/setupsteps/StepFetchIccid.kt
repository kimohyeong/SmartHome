package io.smarthome.mesh.setup.flow.setupsteps

import io.smarthome.mesh.common.Result
import io.smarthome.mesh.common.truthy
import io.smarthome.mesh.setup.connection.ResultCode
import io.smarthome.mesh.setup.flow.MeshSetupFlowException
import io.smarthome.mesh.setup.flow.MeshSetupStep
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.context.SetupContexts
import kotlinx.coroutines.delay


class StepFetchIccid : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        if (ctxs.cellular.targetDeviceIccid.value.truthy()) {
            return
        }

        val targetXceiver = ctxs.requireTargetXceiver()

        val iccidReply = targetXceiver.sendGetIccId()
        when (iccidReply) {
            is Result.Present -> {
                ctxs.cellular.updateTargetDeviceIccid(iccidReply.value.iccid)
            }

            is Result.Error -> {
                if (iccidReply.error == ResultCode.INVALID_STATE) {
                    targetXceiver.sendReset()
                    delay(2000)
                    throw MeshSetupFlowException("INVALID_STATE received while getting ICCID; " +
                            "sending reset command and restarting flow"
                    )
                }
                throw MeshSetupFlowException("Error ${iccidReply.error} when retrieving ICCID")
            }

            is Result.Absent -> {
                throw MeshSetupFlowException("Unknown error when retrieving ICCID")
            }
        }
    }

}