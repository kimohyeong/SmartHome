package io.smarthome.mesh.setup.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.bluetooth.le.ScanFilter.Builder
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.ParcelUuid
import io.smarthome.mesh.bluetooth.BluetoothAdapterStateLD
import io.smarthome.mesh.bluetooth.btAdapter
import io.smarthome.mesh.bluetooth.scanning.BLEScannerLD
import io.smarthome.mesh.bluetooth.scanning.buildReactiveBluetoothScanner
import io.smarthome.mesh.common.AsyncWorkSuspender
import io.smarthome.mesh.common.android.livedata.*
import io.smarthome.mesh.setup.connection.BT_SETUP_SERVICE_ID
import mu.KotlinLogging


private val log = KotlinLogging.logger {}


fun buildMatchingDeviceNameScanner(
        context: Context,
        deviceName: String
): LiveData<List<ScanResult>?> {
    log.info { "Scanning for device $deviceName" }

    val ctx = context.applicationContext

    val toggleScanLD = MutableLiveData<Boolean>()
    toggleScanLD.value = true
    val scannerLD = buildReactiveBluetoothScanner(
            toggleScanLD,
            BluetoothAdapterStateLD(ctx),
            BLEScannerLD(
                    ctx.btAdapter,
                    { sr -> sr.device.name != null && sr.device.name == deviceName },
                    listOf(Builder().setServiceUuid(ParcelUuid(BT_SETUP_SERVICE_ID)).build())
            )
    )

    return scannerLD.distinct()
}


fun buildMatchingDeviceNameSuspender(
        context: Context,
        deviceName: String
): AsyncWorkSuspender<ScanResult?> {
    val scannerLD = buildMatchingDeviceNameScanner(context, deviceName)
    return object: LiveDataSuspender<ScanResult?>() {
        override fun buildLiveData(): LiveData<ScanResult?> {
            return scannerLD.nonNull()
                    .filter { it!!.isNotEmpty() }
                    .map { it!![0] }
        }

    }
}