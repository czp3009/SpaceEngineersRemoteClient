package com.hiczp.spaceengineersremoteclient.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiczp.spaceengineersremoteclient.*
import com.hiczp.spaceengineersremoteclient.layout.defaultToolBar
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

private const val CREATE_REQUEST_CODE = 0
private const val MODIFY_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {
    private val profiles = mutableListOf<Profile>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profiles.addAll(database.use { findAll() })

        verticalLayout {
            appBarLayout {
                defaultToolBar().apply {
                    imageButton(R.drawable.ic_add_white_24dp) {
                        backgroundColor = Color.TRANSPARENT
                    }.onClick {
                        startActivityForResult<ProfileActivity>(CREATE_REQUEST_CODE)
                    }
                }
            }

            recyclerView = recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = ProfileListAdapter(this@MainActivity, profiles)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        val newProfile = data!!.extras!!.get(ProfileActivity.returnValue) as Profile
        val adapter = recyclerView.adapter!!
        when (requestCode) {
            CREATE_REQUEST_CODE -> {
                val lastIndex = profiles.size
                profiles.add(newProfile)
                adapter.notifyItemInserted(lastIndex)
                recyclerView.smoothScrollToPosition(lastIndex)
            }
            MODIFY_REQUEST_CODE -> {
                val index = profiles.indexOfFirst { it.id == newProfile.id }
                profiles[index] = newProfile
                adapter.notifyItemChanged(index)
            }
        }
    }
}

private class ProfileUI : AnkoComponent<ViewGroup> {
    companion object {
        const val nameId = 0
        const val urlId = 1
        const val deleteId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        relativeLayout {
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
            }.lparams {
                alignParentLeft()
            }
            button("Delete") {
                id = deleteId
            }.lparams {
                alignParentRight()
            }
        }
    }
}

private class ProfileListAdapter(
    val activity: Activity,
    val profiles: MutableList<Profile>
) : RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder>() {
    override fun getItemCount() = profiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileViewHolder(ProfileUI().createView(AnkoContext.create(parent.context, parent)))

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        with(holder) {
            name.text = profile.name
            url.text = profile.url
            delete.onClick {
                database.use { deleteById(profile.id!!) }
                val index = profiles.indexOfFirst { it.id == profile.id }
                profiles.removeAt(index)
                notifyItemRemoved(index)
            }
            itemView.onClick {
                activity.startActivity<VRageActivity>(VRageActivity.inputValue to profile)
            }
            itemView.onLongClick {
                activity.startActivityForResult<ProfileActivity>(
                    MODIFY_REQUEST_CODE,
                    ProfileActivity.inputValue to profile
                )
            }
        }
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(ProfileUI.nameId)!!
        val url = itemView.findViewById<TextView>(ProfileUI.urlId)!!
        val delete = itemView.findViewById<Button>(ProfileUI.deleteId)!!
    }
}
