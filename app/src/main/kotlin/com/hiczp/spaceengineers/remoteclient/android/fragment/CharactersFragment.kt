package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.hiczp.spaceengineers.remoteapi.service.session.Character
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.extension.portrait
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class CharactersFragment : Fragment() {
    private lateinit var model: CharactersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[CharactersViewModel::class.java].apply {
            init(client()) { session.characters().data }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var content: TextView
        lateinit var stopButton: Button
        lateinit var refreshButton: Button
        val view = UI {
            verticalLayout {
                scrollView {
                    content = textView()
                }.lparams(weight = 1f)

                if (portrait) {
                    stopButton = button("Stop")
                    refreshButton = button("Refresh")
                } else {
                    linearLayout {
                        stopButton = button("Stop").lparams(weight = 1f)
                        refreshButton = button("Refresh").lparams(weight = 1f)
                    }
                }
            }
        }.view

        model.bindErrorAndRefreshing(
            this,
            stopButton, refreshButton
        )

        model.data.observe(this) { characters ->
            characters.joinToString(separator = "\n", postfix = "\n") {
                "${it.displayName} ${it.linearSpeed}m/s ${it.mass}KG"
            }.run(content::setText)
        }

        stopButton.onClick {

        }
        refreshButton.onClick {
            model.refresh()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        model.tryFirstRefresh()
    }
}

class CharactersViewModel : ClientViewModel<List<Character>>()
