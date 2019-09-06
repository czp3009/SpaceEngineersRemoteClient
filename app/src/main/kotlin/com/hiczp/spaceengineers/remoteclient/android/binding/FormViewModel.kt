package com.hiczp.spaceengineers.remoteclient.android.binding

import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import com.hiczp.spaceengineers.remoteclient.android.extension.parentLifecycleOwner
import kotlin.reflect.KProperty

open class FormViewModel : ViewModel() {
    val form =
        LinkedHashMap<String, Triple<TextView, MutableLiveData<String>, Validator>>()

    operator fun get(fieldName: String) = form[fieldName]?.second?.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get(property.name)!!

    fun validate() = form.asSequence().map { (fieldName, triple) ->
        val (textView, liveData, validator) = triple
        Triple(fieldName, textView, validator(liveData.value ?: ""))
    }.filter { (_, _, error) ->
        error != null
    }.onEach { (_, textView, error) ->
        textView.error = error
    }.toList()

    fun clear() = form.clear()
}

fun <T : TextView> T.bind(
    model: FormViewModel,
    fieldName: String,
    initialValue: String = "",
    lifecycleOwner: LifecycleOwner? = null,
    realtimeValidation: Boolean = false,
    validator: Validator = { null }
) = apply {
    val savedRecord = model.form[fieldName]
    val liveData = if (savedRecord == null) {
        MutableLiveData(initialValue)
    } else {
        MutableLiveData(savedRecord.second.value)
    }
    model.form[fieldName] = Triple(
        this,
        liveData,
        validator
    )
    var shouldDo = false
    (lifecycleOwner ?: parentLifecycleOwner)?.let { actualLifecycleOwner ->
        liveData.observe(actualLifecycleOwner) {
            shouldDo = false
            setTextKeepState(it)
        }
    }
    doAfterTextChanged {
        if (shouldDo) {
            val value = it.toString()
            if (realtimeValidation) error = validator(value)
            liveData.value = value
        } else {
            shouldDo = true
        }
    }
}

fun <T : TextView> T.bind(
    model: FormViewModel,
    fieldName: String,
    initialValue: String = "",
    validator: Validator = { null }
) = bind(model, fieldName, initialValue, null, true, validator)
