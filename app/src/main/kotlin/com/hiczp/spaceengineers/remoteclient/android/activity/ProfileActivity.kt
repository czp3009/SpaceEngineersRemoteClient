package com.hiczp.spaceengineers.remoteclient.android.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hiczp.spaceengineers.remoteclient.android.Profile
import com.hiczp.spaceengineers.remoteclient.android.binding.FormViewModel
import com.hiczp.spaceengineers.remoteclient.android.binding.bind
import com.hiczp.spaceengineers.remoteclient.android.binding.notEmptyValidator
import com.hiczp.spaceengineers.remoteclient.android.database
import com.hiczp.spaceengineers.remoteclient.android.layout.defaultAppBar
import com.hiczp.spaceengineers.remoteclient.android.save
import io.ktor.http.Url
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*
import javax.crypto.spec.SecretKeySpec

class ProfileActivity : AppCompatActivity() {
    private lateinit var model: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[ProfileViewModel::class.java]

        val savedProfile = intent.extras?.get(inputValue) as? Profile
        val savedId = savedInstanceState?.getLong("id") ?: savedProfile?.id
        val savedName = savedProfile?.name ?: ""
        val (savedHost, savedPort) = savedProfile?.let { Url(savedProfile.url) }?.run {
            host to port
        } ?: "" to 8080
        val savedSecurityKey = savedProfile?.securityKey ?: ""

        verticalLayout {
            defaultAppBar()

            verticalLayout {
                padding = dip(16)

                textView("Name:")
                editText {
                    singleLine = true
                }.bind(model, "name", savedName, notEmptyValidator)

                textView("Remote URL:")
                linearLayout {
                    textView("http://") {
                        textColor = Color.BLACK
                    }
                    editText {
                        singleLine = true
                    }.lparams(matchParent).bind(model, "host", savedHost) {
                        if (it.isEmpty()) {
                            "Must not be empty"
                        } else if (it.contains("//") || it.contains(":")) {
                            "Invalid host"
                        } else {
                            null
                        }
                    }
                }

                textView("Port:")
                editText {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    singleLine = true
                    filters += InputFilter.LengthFilter(5)
                }.bind(model, "port", savedPort.toString()) {
                    val range = 1..65535
                    if (it.isEmpty() || it.toInt() !in range) {
                        "Port must in range $range"
                    } else {
                        null
                    }
                }

                textView("Security Key:")
                editText {
                    singleLine = true
                }.bind(model, "securityKey", savedSecurityKey, null, false) {
                    runCatching {
                        SecretKeySpec(Base64.getDecoder().decode(it), "HmacSHA1")
                    }.let {
                        if (it.isFailure) {
                            "Invalid SecurityKey"
                        } else {
                            null
                        }
                    }
                }

                button("Save").onClick {
                    model.saveProfile(savedId)?.run {
                        setResult(
                            Activity.RESULT_OK,
                            intentFor<MainActivity>(returnValue to this)
                        )
                        finish()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val profile = intent.extras?.get(inputValue) as? Profile
        if (profile != null) {
            outState.putLong("id", profile.id!!)
        }
    }

    companion object {
        const val inputValue = "profile"
        const val returnValue = "newProfile"
    }
}

class ProfileViewModel : FormViewModel() {
    fun saveProfile(savedId: Long?): Profile? {
        validate().firstOrNull()?.let { (_, textView, _) ->
            textView.requestFocus()
            return null
        }
        val name by this
        val host by this
        val port by this
        val securityKey by this
        return database.use {
            save(
                Profile(
                    savedId,
                    name,
                    "http://$host:$port",
                    securityKey
                )
            )
        }
    }
}
