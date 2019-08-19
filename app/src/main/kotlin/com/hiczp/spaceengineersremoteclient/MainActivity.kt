package com.hiczp.spaceengineersremoteclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.dip
import org.jetbrains.anko.listView
import org.jetbrains.anko.padding
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            padding = dip(16)

            listView {

            }
        }
    }
}
