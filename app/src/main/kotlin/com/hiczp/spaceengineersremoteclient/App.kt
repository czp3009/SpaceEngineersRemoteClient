package com.hiczp.spaceengineersremoteclient

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

inline val app get() = App.instance
