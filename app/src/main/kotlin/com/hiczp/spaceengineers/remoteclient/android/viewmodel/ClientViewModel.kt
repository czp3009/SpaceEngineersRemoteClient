package com.hiczp.spaceengineers.remoteclient.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

open class ClientViewModel : ViewModel() {
    protected lateinit var client: SpaceEngineersRemoteClient
    val error = MutableLiveData<String>()

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
