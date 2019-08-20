package com.hiczp.spaceengineersremoteclient.layout

import android.graphics.Color.*
import android.view.Gravity.END
import android.view.ViewManager
import com.hiczp.spaceengineersremoteclient.R
import com.hiczp.spaceengineersremoteclient.app
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.toolbar

fun ViewManager.appBar() = appBarLayout {
    toolbar {
        setBackgroundColor(BLACK)
        setTitleTextColor(WHITE)

        title = app.getString(R.string.app_name)
        imageButton(R.drawable.ic_add_white_24dp) {
            backgroundColor = TRANSPARENT
        }.lparams {
            gravity = END
            setMargins(0, 0, 48, 0)
        }.onClick {
            app.toast("Hello")
        }
    }
}
