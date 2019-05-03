// Yes, this file name isn't the right plural, but this fit the naming convention better.
package io.smarthome.mesh.setup

import androidx.annotation.WorkerThread
import io.smarthome.android.sdk.cloud.ParticleCloud
import io.smarthome.android.sdk.cloud.ParticleDevice.ParticleDeviceType
import io.smarthome.mesh.setup.flow.Gen3ConnectivityType
import io.smarthome.mesh.setup.ui.BarcodeData



@WorkerThread
fun BarcodeData.toDeviceType(cloud: ParticleCloud): ParticleDeviceType {
    return this.serialNumber.toDeviceType(cloud)
}


@WorkerThread
fun BarcodeData.toConnectivityType(cloud: ParticleCloud): Gen3ConnectivityType {
    return this.serialNumber.toDeviceType(cloud).toConnectivityType()
}


fun ParticleDeviceType.toConnectivityType(): Gen3ConnectivityType {
    return when (this) {
        ParticleDeviceType.ARGON,
        ParticleDeviceType.A_SERIES -> Gen3ConnectivityType.WIFI
        ParticleDeviceType.BORON,
        ParticleDeviceType.B_SERIES -> Gen3ConnectivityType.CELLULAR
        ParticleDeviceType.XENON,
        ParticleDeviceType.X_SERIES -> Gen3ConnectivityType.MESH_ONLY
        else -> throw IllegalArgumentException("Not a mesh device: $this")
    }
}
