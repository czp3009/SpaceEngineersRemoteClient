package com.hiczp.spaceengineers.remoteclient.android.extension

import com.hiczp.spaceengineers.remoteclient.android.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler

val emptyCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    if (BuildConfig.DEBUG) {
        throwable.printStackTrace()
    }
}
