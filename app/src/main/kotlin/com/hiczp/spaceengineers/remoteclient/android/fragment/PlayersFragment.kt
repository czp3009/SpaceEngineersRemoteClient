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
import com.hiczp.spaceengineers.remoteapi.service.session.Player
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.extension.portrait
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class PlayersFragment : Fragment() {
    private lateinit var model: PlayersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[PlayersViewModel::class.java].apply {
            init(client()) { session.players().data }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var content: TextView
        lateinit var banButton: Button
        lateinit var kickButton: Button
        lateinit var promoteButton: Button
        lateinit var demoteButton: Button
        lateinit var refreshButton: Button
        val view = UI {
            verticalLayout {
                scrollView {
                    content = textView()
                }.lparams(weight = 1f)

                verticalLayout {
                    if (portrait) {
                        linearLayout {
                            banButton = button("Ban").lparams(weight = 1f)
                            kickButton = button("Kick").lparams(weight = 1f)
                        }
                        linearLayout {
                            promoteButton = button("Promote").lparams(weight = 1f)
                            demoteButton = button("Demote").lparams(weight = 1f)
                        }
                        refreshButton = button("Refresh")
                    } else {
                        linearLayout {
                            banButton = button("Ban").lparams(weight = 1f)
                            kickButton = button("Kick").lparams(weight = 1f)
                            promoteButton = button("Promote").lparams(weight = 1f)
                            demoteButton = button("Demote").lparams(weight = 1f)
                            refreshButton = button("Refresh").lparams(weight = 1f)
                        }
                    }
                }
            }
        }.view

        model.bindErrorAndRefreshing(
            this,
            banButton, kickButton, promoteButton, demoteButton, refreshButton
        )

        model.data.observe(this) { players ->
            players.joinToString(separator = "\n", postfix = "\n") {
                "${if (it.factionName.isNotEmpty()) "[${it.factionName}]" else ""} ${it.displayName} ping: ${it.ping}"
            }.run(content::setText)
        }

        refreshButton.onClick {
            model.refresh()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        model.tryFirstRefresh()
    }
}

class PlayersViewModel : ClientViewModel<List<Player>>()
