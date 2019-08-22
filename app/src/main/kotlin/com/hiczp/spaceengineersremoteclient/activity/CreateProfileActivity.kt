package com.hiczp.spaceengineersremoteclient.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.hiczp.spaceengineersremoteclient.Profile
import com.hiczp.spaceengineersremoteclient.database
import com.hiczp.spaceengineersremoteclient.extension.value
import com.hiczp.spaceengineersremoteclient.layout.defaultAppBar
import com.hiczp.spaceengineersremoteclient.save
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*
import javax.crypto.spec.SecretKeySpec

@SuppressLint("SetTextI18n")
class CreateProfileActivity : AppCompatActivity() {
    lateinit var name: EditText
    lateinit var domain: EditText
    lateinit var port: EditText
    lateinit var securityKey: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            defaultAppBar()

            verticalLayout {
                padding = dip(16)

                textView("Name:")
                name = editText()

                textView("Remote URL:")
                linearLayout {
                    textView("http://") {
                        textColor = Color.BLACK
                    }
                    domain = editText().lparams(matchParent)
                }

                textView("Port:")
                port = editText {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    filters += InputFilter.LengthFilter(5)
                    setText("8080")
                }

                textView("Security Key:")
                securityKey = editText()

                button("Save").onClick {
                    runCatching {
                        name.validate()
                        domain.validate()
                        with(port) {
                            validate()
                            val range = 1..65535
                            if (value.toInt() !in range) {
                                error = "Port must in $range"
                                requestFocus()
                                error("")
                            }
                        }
                        with(securityKey) {
                            validate()
                            try {
                                SecretKeySpec(Base64.getDecoder().decode(value), "HmacSHA1")
                            } catch (e: Exception) {
                                error = "Invalid SHA1 secretKey"
                                requestFocus()
                                throw e
                            }
                        }
                    }.onFailure {
                        return@onClick
                    }
                    database.use {
                        save(
                            Profile(
                                name = name.value,
                                url = "http://${domain.value}:${port.value}",
                                securityKey = securityKey.value
                            )
                        )
                    }
                    finish()
                }
            }
        }
    }
}

private fun EditText.validate() {
    if (text.isEmpty()) {
        error = "Must be not empty"
        requestFocus()
        error("")
    }
}
