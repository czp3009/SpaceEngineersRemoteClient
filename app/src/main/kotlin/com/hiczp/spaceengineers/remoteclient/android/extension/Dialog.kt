package com.hiczp.spaceengineers.remoteclient.android.extension

import android.content.DialogInterface
import androidx.fragment.app.Fragment
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

fun Fragment.confirm(message: String, handler: (dialog: DialogInterface) -> Unit) =
    alert(message, "Confirm") {
        yesButton(handler)
        noButton { }
    }.show()
