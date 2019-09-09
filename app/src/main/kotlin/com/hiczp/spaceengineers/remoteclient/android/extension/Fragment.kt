package com.hiczp.spaceengineers.remoteclient.android.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hiczp.spaceengineers.remoteclient.android.activity.VRageViewModel

fun Fragment.vRageViewModel() = ViewModelProvider(activity!!)[VRageViewModel::class.java]

fun Fragment.client() = vRageViewModel().spaceEngineersRemoteClient.value!!
