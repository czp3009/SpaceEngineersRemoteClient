package com.hiczp.spaceengineers.remoteclient.android.extension

import android.view.View
import androidx.lifecycle.LifecycleOwner

val View.parentLifecycleOwner
    get():LifecycleOwner? = when (val context = context) {
        is LifecycleOwner -> context
        else -> null
    }
