package com.hiczp.spaceengineers.remoteclient.android.extension

import android.app.Activity
import android.content.res.Configuration
import org.jetbrains.anko.configuration

val Activity.portrait get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
