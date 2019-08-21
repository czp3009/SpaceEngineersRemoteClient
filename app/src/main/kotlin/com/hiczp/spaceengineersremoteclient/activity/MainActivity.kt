package com.hiczp.spaceengineersremoteclient.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiczp.spaceengineersremoteclient.R
import com.hiczp.spaceengineersremoteclient.layout.defaultToolBar
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            appBarLayout {
                defaultToolBar().apply {
                    imageButton(R.drawable.ic_add_white_24dp) {
                        backgroundColor = Color.TRANSPARENT
                    }.onClick {
                        startActivity<CreateProfileActivity>()
                    }
                }
            }

            listView {

            }
        }
    }
}
