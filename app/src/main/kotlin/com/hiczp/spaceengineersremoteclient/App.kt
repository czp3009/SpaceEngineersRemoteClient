package com.hiczp.spaceengineersremoteclient

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

private class DatabaseHelper(context: Context) : ManagedSQLiteOpenHelper(context, "database") {
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            "profile", true,
            "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "name" to TEXT + NOT_NULL,
            "url" to TEXT + NOT_NULL,
            "port" to INTEGER + NOT_NULL,
            "securityKey" to TEXT + NOT_NULL
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        private var instance: ManagedSQLiteOpenHelper? = null
        fun getInstance(context: Context): ManagedSQLiteOpenHelper {
            if (instance == null) instance = DatabaseHelper(context)
            return instance!!
        }
    }
}

val Context.database: ManagedSQLiteOpenHelper
    get() = DatabaseHelper.getInstance(this)

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
