package com.hiczp.spaceengineersremoteclient.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiczp.spaceengineersremoteclient.Profile
import com.hiczp.spaceengineersremoteclient.R
import com.hiczp.spaceengineersremoteclient.database
import com.hiczp.spaceengineersremoteclient.findAll
import com.hiczp.spaceengineersremoteclient.layout.defaultToolBar
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    private val profiles = database.use { findAll() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            appBarLayout {
                defaultToolBar().apply {
                    imageButton(R.drawable.ic_add_white_24dp) {
                        backgroundColor = Color.TRANSPARENT
                    }.onClick {
                        startActivity<ProfileActivity>()
                    }
                }
            }

            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = ProfileListAdapter(profiles)
            }
        }
    }
}

private class ProfileUI : AnkoComponent<ViewGroup> {
    companion object {
        const val nameId = 0
        const val urlId = 1
    }

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            padding = dip(12)

            textView {
                id = nameId
                textColor = Color.BLACK
            }
            textView {
                id = urlId
            }
        }
    }
}

private class ProfileListAdapter(
    val profiles: List<Profile>
) : RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder>() {
    override fun getItemCount() = profiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileViewHolder(ProfileUI().createView(AnkoContext.create(parent.context, parent)))

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        with(profiles[position]) {
            holder.name.text = name
            holder.url.text = url
            holder.itemView.onClick {

            }
        }
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(ProfileUI.nameId)!!
        val url = itemView.findViewById<TextView>(ProfileUI.urlId)!!
    }
}
