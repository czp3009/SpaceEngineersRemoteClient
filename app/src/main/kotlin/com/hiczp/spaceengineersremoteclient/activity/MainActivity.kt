package com.hiczp.spaceengineersremoteclient.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiczp.spaceengineersremoteclient.layout.appBar
import org.jetbrains.anko.listView
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            appBar()

            listView {

            }
        }
    }
}
