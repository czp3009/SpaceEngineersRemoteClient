package com.hiczp.spaceengineers.remoteclient.android.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.longToast

typealias Error = MutableLiveData<String>

fun Error.bindToToast(fragment: Fragment) = with(fragment) {
    observe(this) {
        longToast(it)
    }
}

open class ClientViewModel : ViewModel() {
    protected lateinit var client: SpaceEngineersRemoteClient
    val error = Error()

    fun init(client: SpaceEngineersRemoteClient) {
        this.client = client
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            context = IO + CoroutineExceptionHandler { _, throwable ->
                error.postValue(throwable.message ?: throwable.toString())
            },
            block = block
        )
}
