package com.hiczp.spaceengineers.remoteclient.android.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hiczp.spaceengineers.remoteclient.android.activity.VRageViewModel

fun Fragment.client() =
    ViewModelProvider(activity!!)[VRageViewModel::class.java].spaceEngineersRemoteClient.value!!
