package com.hiczp.spaceengineers.remoteclient.android.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineers.remoteapi.service.server.Status
import com.hiczp.spaceengineers.remoteapi.service.session.Message
import com.hiczp.spaceengineers.remoteclient.android.Profile
import com.hiczp.spaceengineers.remoteclient.android.adapter.VRageFragmentPagerAdapter
import com.hiczp.spaceengineers.remoteclient.android.extension.error
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager
import java.util.*

private val logger = AnkoLogger<VRageActivity>()

class VRageActivity : AppCompatActivity() {
    private lateinit var model: VRageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val profile = intent.extras!![inputValue] as Profile
        model = ViewModelProvider(this)[VRageViewModel::class.java]

        lateinit var toolbar: Toolbar
        lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager
        verticalLayout {
            appBarLayout {
                toolbar = toolbar {
                    backgroundColor = Color.BLACK
                    setTitleTextColor(Color.WHITE)
                    setSubtitleTextColor(Color.WHITE)
                    title = profile.name
                    subtitle = "Connecting..."
                }
                tabLayout = tabLayout()
            }
            viewPager = viewPager {
                id = View.generateViewId()
                adapter = VRageFragmentPagerAdapter(supportFragmentManager)
            }
        }
        tabLayout.setupWithViewPager(viewPager)

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
    }

    companion object {
        const val inputValue = "profile"
    }
}

class VRageViewModel : ViewModel() {
    val spaceEngineersRemoteClient = MutableLiveData<SpaceEngineersRemoteClient>()
    val error = MutableLiveData<Throwable>()
    val serverStatus = MutableLiveData<Status>()
    val chatMessages = MutableLiveData<MutableList<Message>>(LinkedList())

    init {
        //server status
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
        //chat
        spaceEngineersRemoteClient.observeForever { client ->
            viewModelScope.launch(IO) {
                var lastTimestamp: Long? = null
                while (true) {
                    try {
                        val newMessages = client.session.messages(lastTimestamp).data
                        if (newMessages.isNotEmpty()) {
                            lastTimestamp = newMessages.last().timestamp + 1
                            chatMessages.postValue(chatMessages.value!!.apply { addAll(newMessages) })
                        }
                    } catch (e: CancellationException) {
                        break
                    } catch (e: Exception) {

                    }
                    delay(3_000)
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
