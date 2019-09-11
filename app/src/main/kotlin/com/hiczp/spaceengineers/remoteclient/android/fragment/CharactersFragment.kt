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
import com.hiczp.spaceengineers.remoteapi.service.session.Character
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.bindToToast
import org.jetbrains.anko.button
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class CharactersFragment : Fragment() {
    private lateinit var model: CharactersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[CharactersViewModel::class.java].apply {
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

                button("Refresh").onClick {
                    model.refresh()
                }
            }
        }.view

        model.error.bindToToast(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        if (model.characters.value == null) {
            model.refresh()
        }
    }
}

class CharactersViewModel : ClientViewModel() {
    val characters = MutableLiveData<List<Character>>()

    fun refresh() {
        launch {
            client.session.characters().data.run(characters::postValue)
        }
    }
}
