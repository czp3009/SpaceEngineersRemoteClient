package com.hiczp.spaceengineers.remoteclient.android.extension

import android.view.ViewManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.recyclerview.v7._RecyclerView
import org.jetbrains.anko.recyclerview.v7.recyclerView

inline fun ViewManager.horizontalRecyclerView(crossinline init: (@AnkoViewDslMarker _RecyclerView).() -> Unit = {}) =
    recyclerView {
        layoutManager = LinearLayoutManager(context)
        init()
    }
