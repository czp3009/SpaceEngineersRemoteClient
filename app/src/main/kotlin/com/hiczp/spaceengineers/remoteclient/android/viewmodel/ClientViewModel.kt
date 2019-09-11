package com.hiczp.spaceengineers.remoteclient.android.viewmodel

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.hiczp.spaceengineers.remoteapi.SpaceEngineersRemoteClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.longToast

private typealias Error = MutableLiveData<String>

private typealias RefreshingLiveData = MutableLiveData<Boolean>

open class ClientViewModel<T> : ViewModel() {
    lateinit var client: SpaceEngineersRemoteClient
    private lateinit var refreshAction: suspend SpaceEngineersRemoteClient.() -> T
    val data = MutableLiveData<T>()
    val refreshing = RefreshingLiveData(false)
    val error = Error()

    fun init(
        client: SpaceEngineersRemoteClient,
        refreshAction: suspend SpaceEngineersRemoteClient.() -> T
    ) {
        this.client = client
        this.refreshAction = refreshAction
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            context = IO + CoroutineExceptionHandler { _, throwable ->
                error.postValue(throwable.message ?: throwable.toString())
            },
            block = block
        )

    fun refresh() = launch {
        refreshing.postValue(true)
        data.postValue(client.refreshAction())
    }.apply {
        invokeOnCompletion { refreshing.postValue(false) }
    }

    fun tryFirstRefresh() {
        if (data.value == null) refresh()
    }

    fun bindError(fragment: Fragment) = error.bindToToast(fragment)

    fun bindRefreshing(lifecycleOwner: LifecycleOwner, vararg view: View) =
        refreshing.bindToView(lifecycleOwner, *view)

    fun bindErrorAndRefreshing(fragment: Fragment, vararg view: View) {
        bindError(fragment)
        bindRefreshing(fragment, *view)
    }
}

fun Error.bindToToast(fragment: Fragment) = with(fragment) {
    observe(this) {
        longToast(it)
    }
}

fun RefreshingLiveData.bindToView(lifecycleOwner: LifecycleOwner, vararg view: View) =
    observe(lifecycleOwner) { refreshing ->
        view.forEach { it.isEnabled = !refreshing }
    }
