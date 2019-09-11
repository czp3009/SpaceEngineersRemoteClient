package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.hiczp.spaceengineers.remoteapi.service.NoDataResponse
import com.hiczp.spaceengineers.remoteclient.android.extension.client
import com.hiczp.spaceengineers.remoteclient.android.extension.confirm
import com.hiczp.spaceengineers.remoteclient.android.viewmodel.ClientViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.runOnUiThread
import java.time.LocalDateTime

class ExtraFragment : Fragment() {
    private lateinit var model: ExtraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model = ViewModelProvider(this)[ExtraViewModel::class.java].apply {
            init(client()) { server.stopServer() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var stopServerButton: Button
        lateinit var note: TextView
        val view = UI {
            verticalLayout {
                padding = dip(10)

                textView("Warn: You may need to manually restart the server") {
                    textColor = Color.RED
                }
                stopServerButton = button("Stop Server")
                note = textView()
            }
        }.view

        model.bindErrorAndRefreshing(
            this,
            stopServerButton
        )

        stopServerButton.onClick {
            confirm("You are trying to shut down the server") {
                model.refresh().invokeOnCompletion {
                    if (it == null) {
                        model.invokeTime.postValue(LocalDateTime.now())
                        runOnUiThread { longToast("Stopping server...") }
                    }
                }
            }
        }

        model.invokeTime.observe(this) {
            @SuppressLint("SetTextI18n")
            note.text = "Invoked at $it"
        }

        return view
    }
}

class ExtraViewModel : ClientViewModel<NoDataResponse>() {
    val invokeTime = MutableLiveData<LocalDateTime>()
}
