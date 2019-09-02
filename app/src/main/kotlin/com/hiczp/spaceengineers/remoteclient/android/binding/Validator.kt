package com.hiczp.spaceengineers.remoteclient.android.binding

typealias Validator = (String) -> String?

val notEmptyValidator: Validator = { text -> if (text.isEmpty()) "Must not be empty" else null }
