package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.bindToToast
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert

class ExtraFragment : Fragment() {
    private lateinit var model: ExtraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[ExtraViewModel::class.java].apply {
            init(client())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var stopServerButton: Button
        val view = UI {
            verticalLayout {
                padding = dip(10)

                textView("Warn: You may need to manually restart the server") {
                    textColor = Color.RED
                }
                stopServerButton = button("Stop Server")
            }
        }.view

        model.error.bindToToast(this)

        model.stopping.observe(this) {
            stopServerButton.isEnabled = !it
        }
        stopServerButton.onClick {
            alert("You are trying to shut down the server", "Confirm") {
                yesButton {
                    model.stopServer()
                }
                noButton { }
            }.show()
        }

        return view
    }
}

class ExtraViewModel : ClientViewModel() {
    val stopping = MutableLiveData(false)

    fun stopServer() {
        stopping.value = true
        launch {
            client.server.stopServer()
        }.invokeOnCompletion {
            if (it != null) stopping.postValue(false)
        }
    }
}
