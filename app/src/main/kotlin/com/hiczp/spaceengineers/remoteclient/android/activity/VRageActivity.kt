package com.hiczp.spaceengineers.remoteclient.android.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineers.remoteapi.service.server.Status
import com.hiczp.spaceengineers.remoteclient.android.Profile
import com.hiczp.spaceengineers.remoteclient.android.R
import com.hiczp.spaceengineers.remoteclient.android.extension.error
import io.ktor.client.engine.okhttp.OkHttp
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
        model = ViewModelProvider(this)[VRageViewModel::class.java]

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
            verticalLayout {
                horizontalPadding = dip(5)
                id = fragmentContainerId
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
        model.init(profile)

        supportFragmentManager.commit {
            replace(
                fragmentContainerId,
                supportFragmentManager.findFragmentByTag(fragmentTag)
                    ?: NavHostFragment.create(R.navigation.server_indicator_navigation).apply {
                        retainInstance = true
                    },
                fragmentTag
            )
        }
    }

    companion object {
        const val inputValue = "profile"
        const val fragmentContainerId = 1
        const val fragmentTag = "nav_host"
    }
}

class VRageViewModel : ViewModel() {
    val spaceEngineersRemoteClient = MutableLiveData<SpaceEngineersRemoteClient>()
    val error = MutableLiveData<Throwable>()
    val serverStatus = MutableLiveData<Status>()

    init {
        spaceEngineersRemoteClient.observeForever {
            viewModelScope.launch(IO + CoroutineExceptionHandler { _, throwable ->
                logger.error(throwable)
                error.postValue(throwable)
            }) {
                while (true) {
                    it.server.serverStatus().data.run(serverStatus::postValue)
                    delay(10_000)
                }
            }
        }
    }

    fun init(profile: Profile) {
        if (spaceEngineersRemoteClient.value == null) {
            spaceEngineersRemoteClient.value = SpaceEngineersRemoteClient(
                profile.url,
                profile.securityKey,
                OkHttp
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        spaceEngineersRemoteClient.value?.close()
    }
}
