package com.hiczp.spaceengineersremoteclient.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineersremoteclient.Profile
import com.hiczp.spaceengineersremoteclient.extension.emptyCoroutineExceptionHandler
import com.hiczp.spaceengineersremoteclient.extension.value
import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk27.coroutines.onClick

class VRageActivity : AppCompatActivity() {
    private lateinit var spaceEngineersRemoteClient: SpaceEngineersRemoteClient
    private lateinit var toolbar: Toolbar
    private lateinit var chatTextView: TextView
    private var heartbeatJob: Job? = null
    private var messageJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val profile = intent.extras!![inputValue] as Profile
        spaceEngineersRemoteClient = SpaceEngineersRemoteClient(profile.url, profile.securityKey)

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
                horizontalPadding = dip(8)

                scrollView {
                    chatTextView = textView {
                        textColor = Color.BLACK
                    }
                }

                linearLayout {
                    val chatEditText = editText {
                        singleLine = true
                    }.lparams(weight = 1f)
                    button("Send").onClick {
                        val text = chatEditText.value
                        chatEditText.setText("")
                        CoroutineScope(IO).launch(emptyCoroutineExceptionHandler) {
                            spaceEngineersRemoteClient.session.sendMessage(text)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        heartbeatJob = CoroutineScope(IO).launch {
            try {
                while (true) {
                    spaceEngineersRemoteClient.server.serverStatus().data.run {
                        runOnUiThread {
                            toolbar.subtitle =
                                "Sim: $simSpeed, load: ${simulationCpuLoad.toInt()}%, Players: $players"
                        }
                    }
                    delay(heartbeatInterval)
                }
            } catch (clientRequestException: ClientRequestException) {
                runOnUiThread {
                    alert("Please check securityKey") {
                        title = "Forbidden"
                        yesButton { finish() }
                        onCancelled { finish() }
                    }.show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    alert(e.message ?: e.toString()) {
                        title = "Error"
                        yesButton { finish() }
                        onCancelled { finish() }
                    }.show()
                }
            }
        }

        messageJob = CoroutineScope(IO).launch {
            var lastTimestamp: Long? = null
            while (true) {
                try {
                    spaceEngineersRemoteClient.session.messages(lastTimestamp).data.run {
                        runOnUiThread {
                            chatTextView.append(joinToString(separator = "\n") { "${it.displayName}: ${it.content}" })
                        }
                        lastTimestamp = last().timestamp
                    }
                    delay(chatInterval)
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        heartbeatJob?.cancel()
        messageJob?.cancel()
    }

    companion object {
        private const val heartbeatInterval = 61_000L
        private const val chatInterval = 2_000L
        const val inputValue = "profile"
    }
}
