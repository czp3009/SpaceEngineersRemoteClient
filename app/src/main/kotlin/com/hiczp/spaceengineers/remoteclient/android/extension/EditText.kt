package com.hiczp.spaceengineers.remoteclient.android.extension

import android.widget.EditText

inline val EditText.value get() = text.toString()
