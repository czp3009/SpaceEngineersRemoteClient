package com.hiczp.spaceengineers.remoteclient.android.layout

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.view.View
import android.view.ViewManager
import com.hiczp.spaceengineers.remoteclient.android.R
import com.hiczp.spaceengineers.remoteclient.android.app
import org.jetbrains.anko._Toolbar
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toolbar

fun _Toolbar.defaultStyle() {
    backgroundColor = BLACK
    setTitleTextColor(WHITE)
    title = app.getString(R.string.app_name)
}

fun ViewManager.defaultAppBar() = appBarLayout {
    id = View.generateViewId()
    toolbar {
        defaultStyle()
    }.lparams(matchParent)
}
