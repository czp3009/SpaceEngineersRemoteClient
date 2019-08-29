package com.hiczp.spaceengineersremoteclient

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import java.io.Serializable

data class Profile(
    var id: Long? = null,
    val name: String,
    val url: String,
    val securityKey: String
) : Serializable

private const val databaseName = "database"
private const val tableName = "profile"

class DatabaseHelper(context: Context) : ManagedSQLiteOpenHelper(
    ctx = context,
    name = databaseName,
    version = 1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            tableName, true,
            "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "name" to TEXT + NOT_NULL,
            "url" to TEXT + NOT_NULL,
            "securityKey" to TEXT + NOT_NULL
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}

fun SQLiteDatabase.save(profile: Profile) = with(profile) {
    val id = profile.id
    val fields = arrayOf(
        "name" to name,
        "url" to url,
        "securityKey" to securityKey
    )
    if (id == null) {
        insert(tableName, *fields).also {
            profile.id = it
        }
    } else {
        update(tableName, *fields).whereSimple("id=?", id.toString()).exec()
    }
    profile
}

private val profileParser = classParser<Profile>()

fun SQLiteDatabase.findAll() = select(tableName).exec {
    parseList(profileParser)
}

fun SQLiteDatabase.deleteById(id: Long) = delete(tableName, "id=?", arrayOf(id.toString()))
