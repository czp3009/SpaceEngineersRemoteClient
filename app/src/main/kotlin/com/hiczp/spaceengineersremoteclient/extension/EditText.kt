package com.hiczp.spaceengineersremoteclient.extension

import android.widget.EditText

inline val EditText.value get() = text.toString()
