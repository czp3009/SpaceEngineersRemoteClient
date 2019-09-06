package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineers.remoteapi.service.session.Message
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView

private lateinit var spaceEngineersRemoteClient: SpaceEngineersRemoteClient

class ChatFragment : Fragment() {
    private lateinit var model: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        spaceEngineersRemoteClient = client()
        model = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var textView: TextView
        val view = UI {
            scrollView {
                textView = textView()
            }
        }.view

        model.newChatMessage.observe(this@ChatFragment) { messages ->
            textView.append(messages.joinToString(separator = "\n") {
                "${it.timestamp} [${it.displayName}]: ${it.content}"
            })
        }
        return view
    }
}

class ChatViewModel : ViewModel() {
    val newChatMessage = MutableLiveData(emptyList<Message>())

    init {
        viewModelScope.launch(IO) {
            var lastTimestamp: Long? = null
            while (true) {
                try {
                    spaceEngineersRemoteClient.session.messages(lastTimestamp)
                        .data.also { messages ->
                        messages.lastOrNull()?.let { lastTimestamp = it.timestamp + 1 }
                    }.run(newChatMessage::postValue)
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {

                }
                delay(3_000)
            }
        }
    }
}
