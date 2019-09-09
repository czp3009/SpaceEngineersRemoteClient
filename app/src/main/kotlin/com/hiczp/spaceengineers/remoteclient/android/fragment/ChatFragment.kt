package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.hiczp.spaceengineers.remoteclient.android.activity.VRageViewModel
import com.hiczp.spaceengineers.remoteclient.android.extension.vRageViewModel
import org.jetbrains.anko.dip
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalPadding

class ChatFragment : Fragment() {
    private lateinit var vRageViewModel: VRageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        vRageViewModel = vRageViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var textView: TextView
        val view = UI {
            scrollView {
                verticalPadding = dip(5)
                textView = textView()
            }
        }.view

        var previousLine = 0
        vRageViewModel.chatMessages.observe(this@ChatFragment) { messages ->
            if (messages.size > previousLine) {
                val currentLine = messages.size
                textView.append(
                    messages.subList(previousLine, currentLine).joinToString(separator = "\n") {
                        "${it.timestamp} [${it.displayName}]: ${it.content}"
                    }
                )
                previousLine = currentLine
            }
        }
        return view
    }
}
