package com.hiczp.spaceengineersremoteclient.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.hiczp.spaceengineersremoteclient.database
import com.hiczp.spaceengineersremoteclient.layout.defaultAppBar
import org.jetbrains.anko.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.sdk27.coroutines.onClick

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
                    securityKey.validate()
                    with(port) {
                        if (text.isEmpty() || text.toString().toInt() !in 1..65535) {
                            error = "Port must in range 1..65535"
                            requestFocus()
                        }
                    }
                    domain.validate()
                    name.validate()

                    database.use {
                        insert(
                            "profile",
                            "name" to name.text.toString(),
                            "url" to "http://${domain.text}",
                            "port" to port.text.toString().toInt(),
                            "securityKey" to securityKey.toString()
                        )
                    }
                }
            }
        }
    }
}

private fun EditText.validate() {
    if (text.isEmpty()) {
        error = "Must be not empty"
        requestFocus()
    }
}
