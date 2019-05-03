package io.smarthome.mesh.common.android

import android.content.BroadcastReceiver
import android.content.IntentFilter


fun BroadcastReceiver.filterFromAction(bcastAction: String): IntentFilter = IntentFilter(bcastAction)
