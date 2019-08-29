package com.hiczp.spaceengineersremoteclient.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineers.remoteapi.service.server.Status
import com.hiczp.spaceengineersremoteclient.Profile
import com.hiczp.spaceengineersremoteclient.extension.error
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout

private val logger = AnkoLogger<VRageActivity>()

class VRageActivity : AppCompatActivity() {
    private lateinit var model: VRageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val profile = intent.extras!![inputValue] as Profile
        model = ViewModelProvider(this)[VRageViewModel::class.java].apply {
            init(profile)
        }

        lateinit var toolbar: Toolbar
        verticalLayout {
            appBarLayout {
                toolbar = toolbar {
                    backgroundColor = Color.BLACK
                    setTitleTextColor(Color.WHITE)
                    setSubtitleTextColor(Color.WHITE)
                    title = profile.name
                    subtitle = "Connecting..."
                }
            }
        }

        model.error.observe(this) {
            alert(it.message ?: it.toString()) {
                title = "Error"
                yesButton { finish() }
                onCancelled { finish() }
            }.show()
        }
        model.serverStatus.observe(this) {
            toolbar.subtitle =
                "Sim: ${it.simSpeed}, load: ${it.simulationCpuLoad.toInt()}%, Players: ${it.players}"
        }
        model.startFetchServerStatus()
    }

    companion object {
        const val inputValue = "profile"
    }
}

class VRageViewModel : ViewModel() {
    private lateinit var spaceEngineersRemoteClient: SpaceEngineersRemoteClient
    val error = MutableLiveData<Throwable>()
    val serverStatus = MutableLiveData<Status>()

    fun init(profile: Profile) {
        spaceEngineersRemoteClient = SpaceEngineersRemoteClient(profile.url, profile.securityKey)
    }

    fun startFetchServerStatus(interval: Long = 5_000) {
        viewModelScope.launch(IO + CoroutineExceptionHandler { _, throwable ->
            logger.error(throwable)
            error.postValue(throwable)
        }) {
            while (true) {
                serverStatus.postValue(spaceEngineersRemoteClient.server.serverStatus().data)
                delay(interval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        spaceEngineersRemoteClient.close()
    }
}
