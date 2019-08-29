package com.hiczp.spaceengineersremoteclient.extension

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error

fun AnkoLogger.error(throwable: Throwable) {
    error(throwable.message, throwable)
}
