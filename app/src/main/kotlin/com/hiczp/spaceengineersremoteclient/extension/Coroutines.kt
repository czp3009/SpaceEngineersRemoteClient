package com.hiczp.spaceengineersremoteclient.extension

import com.hiczp.spaceengineersremoteclient.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler

val emptyCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    if (BuildConfig.DEBUG) {
        throwable.printStackTrace()
    }
}
