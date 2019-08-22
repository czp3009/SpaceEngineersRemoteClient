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
class ProfileActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var domain: EditText
    private lateinit var port: EditText
    private lateinit var securityKey: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedProfile = intent.extras?.get("profile") as? Profile

        verticalLayout {
            defaultAppBar()

            verticalLayout {
                padding = dip(16)

                textView("Name:")
                name = editText {
                    setText(savedProfile?.name)
                }

                textView("Remote URL:")
                linearLayout {
                    textView("http://") {
                        textColor = Color.BLACK
                    }
                    domain = editText {
                        setText(savedProfile?.url?.substringAfter("http://"))
                    }.lparams(matchParent)
                }

                textView("Port:")
                port = editText {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    filters += InputFilter.LengthFilter(5)
                    setText(savedProfile?.url?.substringAfterLast(":") ?: "8080")
                }

                textView("Security Key:")
                securityKey = editText {
                    setText(savedProfile?.securityKey)
                }

                button("Save").onClick {
                    fun EditText.checkEmpty() {
                        if (text.isEmpty()) throw ValidationException(this, "Must be not empty")
                    }
                    try {
                        name.checkEmpty()
                        domain.checkEmpty()
                        with(port) {
                            checkEmpty()
                            val range = 1..65535
                            if (value.toInt() !in range) throw ValidationException(
                                this,
                                "Port must in $range"
                            )
                        }
                        with(securityKey) {
                            checkEmpty()
                            try {
                                SecretKeySpec(Base64.getDecoder().decode(value), "HmacSHA1")
                            } catch (e: Exception) {
                                throw ValidationException(this, "Invalid SHA1 secretKey")
                            }
                        }
                    } catch (validationException: ValidationException) {
                        val (editText, errorMessage) = validationException
                        editText.run {
                            error = errorMessage
                            requestFocus()
                        }
                        return@onClick
                    }
                    database.use {
                        save(
                            Profile(
                                id = savedProfile?.id,
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

class ValidationException(
    private val editText: EditText,
    private val errorMessage: String
) : IllegalStateException() {
    operator fun component1() = editText
    operator fun component2() = errorMessage
}
