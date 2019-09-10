package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import com.hiczp.spaceengineers.remoteclient.android.activity.VRageViewModel
import com.hiczp.spaceengineers.remoteclient.android.binding.FormViewModel
import com.hiczp.spaceengineers.remoteclient.android.binding.bind
import com.hiczp.spaceengineers.remoteclient.android.extension.emptyCoroutineExceptionHandler
import com.hiczp.spaceengineers.remoteclient.android.extension.toLocalDateTime
import com.hiczp.spaceengineers.remoteclient.android.extension.vRageViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class ChatFragment : Fragment() {
    private lateinit var vRageViewModel: VRageViewModel
    private lateinit var model: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        vRageViewModel = vRageViewModel()
        model = ViewModelProvider(this)[ChatViewModel::class.java].apply {
            init(vRageViewModel.client)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var content: TextView
        lateinit var sendButton: Button
        val view = UI {
            verticalLayout {
                scrollView {
                    horizontalPadding = dip(5)
                    content = textView {
                        hint = "Loading..."
                    }
                }.lparams(weight = 1f)

                linearLayout {
                    editText {
                        singleLine = true
                    }.lparams(weight = 1f).bind(
                        model = model,
                        fieldName = "input"
                    )
                    sendButton = button("Send")
                }
            }
        }.view

        var previousLine = 0
        vRageViewModel.chatMessages.observe(this@ChatFragment) { messages ->
            if (messages.size > previousLine) {
                val currentLine = messages.size
                content.append(
                    messages.subList(previousLine, currentLine).joinToString(
                        separator = "\n",
                        postfix = "\n"
                    ) {
                        "${it.timestamp.toLocalDateTime()} [${it.displayName}]: ${it.content}"
                    }
                )
                previousLine = currentLine
            }
        }

        model.sendButtonEnable.observe(this@ChatFragment) {
            sendButton.isEnabled = it
        }
        sendButton.onClick {
            val inputText = model["input"]!!
            model["input"] = ""
            model.sendMessage(inputText)
        }

        return view
    }
}

//no multiple inheritance
class ChatViewModel : FormViewModel() {
    private lateinit var client: SpaceEngineersRemoteClient
    val sendButtonEnable by lazy { form["input"]!!.second.map { it.isNotEmpty() } }

    fun init(client: SpaceEngineersRemoteClient) {
        this.client = client
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(IO + emptyCoroutineExceptionHandler) {
            client.session.sendMessage(message)
        }
    }
}
