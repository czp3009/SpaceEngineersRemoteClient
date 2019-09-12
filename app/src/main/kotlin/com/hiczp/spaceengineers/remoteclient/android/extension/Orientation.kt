package com.hiczp.spaceengineers.remoteclient.android.extension

import android.app.Activity
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import org.jetbrains.anko.configuration

val Activity.portrait get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Activity.landscape get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

val Fragment.portrait get() = requireActivity().portrait

val Fragment.landscape get() = requireActivity().landscape
