package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.hiczp.spaceengineers.remoteapi.service.session.Player
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import org.jetbrains.anko.button
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class PlayerFragment : Fragment() {
    private lateinit var model: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[PlayerViewModel::class.java].apply {
            init(client())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var content: TextView
        lateinit var refreshButton: Button
        val view = UI {
            verticalLayout {
                scrollView {
                    content = textView()
                }.lparams(weight = 1f)

                refreshButton = button("Refresh")
            }
        }.view

        model.refreshing.observe(this) {
            refreshButton.isEnabled = !it
        }
        refreshButton.onClick {
            model.refresh()
        }

        model.players.observe(this) { players ->
            players.joinToString(separator = "\n", postfix = "\n") {
                "${if (it.factionName.isNotEmpty()) "[${it.factionName}]" else ""} ${it.displayName} ping: ${it.ping}"
            }.run(content::setText)
        }

        model.error.observe(this) {
            longToast(it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (model.players.value == null) {
            model.refresh()
        }
    }
}

class PlayerViewModel : ClientViewModel() {
    val players = MutableLiveData<List<Player>>()
    val refreshing = MutableLiveData(false)

    fun refresh() {
        refreshing.value = true
        launch {
            client.session.players().data.run(players::postValue)
        }.invokeOnCompletion { refreshing.postValue(false) }
    }
}
