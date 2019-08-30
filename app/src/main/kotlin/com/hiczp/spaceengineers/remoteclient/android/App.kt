package com.hiczp.spaceengineers.remoteclient.android

import android.app.Application
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        //init
        instance = this
        database = DatabaseHelper(this)
    }

    companion object {
        lateinit var instance: App
            private set
        lateinit var database: ManagedSQLiteOpenHelper
            private set
    }
}

inline val app get() = App.instance
inline val database get() = App.database
