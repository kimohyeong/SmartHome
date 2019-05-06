package io.smarthome.mesh.setup.flow.modules.cloudconnection

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.smarthome.firmwareprotos.ctrl.wifi.WifiNew
import io.smarthome.mesh.common.Result
import io.smarthome.mesh.common.android.livedata.setOnMainThread
import io.smarthome.mesh.common.truthy
import io.smarthome.mesh.setup.connection.ProtocolTransceiver
import io.smarthome.mesh.setup.flow.Scopes
import kotlinx.coroutines.delay
import mu.KotlinLogging


typealias WifiScanData = WifiNew.ScanNetworksReply.Network


class WifiNetworksScannerLD(
    private val targetXceiverLD: LiveData<ProtocolTransceiver?>,
    private val scopes: Scopes = Scopes()
) : MutableLiveData<List<WifiScanData>?>() {

    private val log = KotlinLogging.logger {}

    override fun onActive() {
        super.onActive()
        log.debug { "onActive()" }
        scopes.onWorker {
            scanWhileActive()
        }
    }

    fun forceSingleScan() {
        log.debug { "forceSingleScan()" }
        scopes.onWorker {
            doScan()
        }
    }

    private suspend fun scanWhileActive() {
        while (hasActiveObservers()) {
            doScan()
            delay(10000)
        }
    }

    @WorkerThread
    private suspend fun doScan() {
        log.debug { "doScan()" }
        val xceiver = targetXceiverLD.value!!
        val networksReply = xceiver.sendScanWifiNetworks()
        val networks = when(networksReply) {
            is Result.Present -> networksReply.value.networksList
            is Result.Error,
            is Result.Absent -> {
                listOf()
            }
        }
        if (value.truthy() && networks.isEmpty()) {
            return  // don't replace results with non results (at least for MVP)
        }

        setOnMainThread(networks)
    }

}