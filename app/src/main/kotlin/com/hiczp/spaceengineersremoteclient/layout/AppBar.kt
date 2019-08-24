package com.hiczp.spaceengineersremoteclient.layout

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.view.ViewManager
import com.hiczp.spaceengineersremoteclient.R
import com.hiczp.spaceengineersremoteclient.app
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design._AppBarLayout
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.toolbar

fun _AppBarLayout.defaultToolBar() = toolbar {
    backgroundColor = BLACK
    setTitleTextColor(WHITE)
    title = app.getString(R.string.app_name)
}

fun ViewManager.defaultAppBar() = appBarLayout {
    defaultToolBar()
}
