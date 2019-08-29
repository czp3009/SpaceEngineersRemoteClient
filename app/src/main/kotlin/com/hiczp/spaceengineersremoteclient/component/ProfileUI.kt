package com.hiczp.spaceengineersremoteclient.component

import android.graphics.Color
import android.view.ViewGroup
import org.jetbrains.anko.*

class ProfileUI : AnkoComponent<ViewGroup> {
    companion object {
        const val nameId = 0
        const val urlId = 1
        const val deleteId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            lparams(matchParent)
            padding = dip(12)

            verticalLayout {
                textView {
                    id = nameId
                    textSize = 18f
                    textColor = Color.BLACK
                }
                textView {
                    id = urlId
                }
            }.lparams(weight = 1f)

            button("Delete") {
                id = deleteId
            }
        }
    }
}
