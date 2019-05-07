package io.smarthome.android.sdk.controlpanel

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import io.smarthome.android.sdk.cloud.ParticleCloudSDK
import io.smarthome.mesh.common.android.livedata.ClearValueOnInactiveLiveData
import io.smarthome.mesh.common.android.livedata.castAndPost
import io.smarthome.mesh.common.android.livedata.castAndSetOnMainThread
import io.smarthome.mesh.setup.flow.DialogTool
import io.smarthome.mesh.setup.flow.MeshSetupFlowRunner
import io.smarthome.mesh.setup.flow.Scopes
import io.smarthome.mesh.setup.flow.buildFlowManager
import io.smarthome.mesh.setup.flow.modules.FlowUiDelegate
import io.smarthome.mesh.setup.ui.DialogSpec
import io.smarthome.mesh.setup.ui.ProgressHack
import mu.KotlinLogging

// FIXME: this shouldn't have much of anything here -- wrap all this logic/members into a helper class
class MeshManagerAccessModel(private val app: Application) : ProgressHack, AndroidViewModel(app) {

    companion object {

        fun getViewModel(activity: FragmentActivity): MeshManagerAccessModel {
            return ViewModelProviders.of(activity)
                .get(MeshManagerAccessModel::class.java)
        }

        fun getViewModel(fragment: Fragment): MeshManagerAccessModel {
            val activity = fragment.requireActivity()
            return getViewModel(activity)
        }
    }

    private val log = KotlinLogging.logger {}

    lateinit var flowRunner: MeshSetupFlowRunner

    val scopes = Scopes()
    val dialogRequestLD: LiveData<DialogSpec?> = ClearValueOnInactiveLiveData<DialogSpec>()
    val dialogHack = DialogTool(dialogRequestLD, MutableLiveData())
    val shouldTerminateLD: LiveData<Boolean?> = ClearValueOnInactiveLiveData<Boolean>()
    val shouldShowProgressSpinnerLD: LiveData<Boolean?> = MutableLiveData()

    var navControllerLD: LiveData<NavController?> = MutableLiveData()

    fun initialize(flowUiDelegate: FlowUiDelegate) {
        flowRunner = buildFlowManager(
            app,
            ParticleCloudSDK.getCloud(),
            dialogHack,
            flowUiDelegate
        )
    }

    fun terminateSetup() {
        log.info { "terminateSetup()" }
        shouldTerminateLD.castAndPost(true)
    }

    fun setNavController(navController: NavController?) {
        navControllerLD.castAndSetOnMainThread(navController)
    }

    override fun showGlobalProgressSpinner(show: Boolean) {
        shouldShowProgressSpinnerLD.castAndPost(show)
    }

    override fun onCleared() {
        super.onCleared()
        setNavController(null)
        scopes.cancelAll()
    }

}