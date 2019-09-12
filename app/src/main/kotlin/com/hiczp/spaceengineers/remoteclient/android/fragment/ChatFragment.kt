package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ScrollView
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
import org.jetbrains.anko.sdk27.coroutines.onLayoutChange
import org.jetbrains.anko.sdk27.coroutines.onScrollChange
import org.jetbrains.anko.support.v4.UI

class ChatFragment : Fragment() {
    private lateinit var vRageViewModel: VRageViewModel
    private lateinit var model: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        vRageViewModel = vRageViewModel()
        model = ViewModelProvider(this)[ChatViewModel::class.java].apply {
            init(vRageViewModel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var scrollView: ScrollView
        lateinit var content: TextView
        lateinit var sendButton: Button
        val view = UI {
            verticalLayout {
                scrollView = scrollView {
                    horizontalPadding = dip(5)

                    content = textView {
                        hint = "Loading..."
                    }
                }.lparams {
                    width = matchParent
                    weight = 1f
                }

                linearLayout {
                    editText {
                        singleLine = true
                    }.lparams(weight = 1f).bind(model = model, fieldName = "input")
                    sendButton = button("Send")
                }
            }
        }.view

        //input method
        var inputMethodOpen = false
        var previousBottomDifference = 0
        scrollView.onScrollChange { _, _, scrollY, _, _ ->
            if (!inputMethodOpen) {
                previousBottomDifference = content.bottom - (scrollView.height + scrollY)
            }
        }
        scrollView.onLayoutChange { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (oldBottom != 0 && oldBottom != bottom) {
                scrollView.scrollTo(
                    scrollView.scrollX,
                    content.bottom - scrollView.height - previousBottomDifference
                )
                inputMethodOpen = oldBottom > bottom
            }
        }

        var previousLine = 0
        vRageViewModel.chatMessages.observe(this@ChatFragment) { messages ->
            if (content.hint.isNotEmpty()) content.hint = ""
            if (messages.size > previousLine) {
                val nowInEnd = content.bottom == scrollView.height + scrollView.scrollY
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
                //auto scroll to end if in end before append
                if (nowInEnd) scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }

        model.sendButtonEnable.observe(this) {
            sendButton.isEnabled = it
        }
        sendButton.onClick {
            val inputText = model["input"]!!
            model["input"] = ""
            model.sendMessage(inputText)
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        val windowToken = requireActivity().currentFocus?.windowToken
        if (windowToken != null) {
            requireContext().getSystemService(InputMethodManager::class.java)
                .hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

//no multiple inheritance
class ChatViewModel : FormViewModel() {
    private lateinit var vRageViewModel: VRageViewModel
    private lateinit var client: SpaceEngineersRemoteClient
    val sendButtonEnable by lazy { form["input"]!!.second.map { it.isNotEmpty() } }

    fun init(vRageViewModel: VRageViewModel) {
        this.vRageViewModel = vRageViewModel
        this.client = vRageViewModel.client
    }

    fun sendMessage(message: String) = viewModelScope.launch(IO + emptyCoroutineExceptionHandler) {
        client.session.sendMessage(message)
        vRageViewModel.chatMessagePulse.send(Unit)
    }
}
