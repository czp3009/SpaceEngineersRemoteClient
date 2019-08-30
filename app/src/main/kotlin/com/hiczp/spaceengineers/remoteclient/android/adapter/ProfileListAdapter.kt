package com.hiczp.spaceengineers.remoteclient.android.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hiczp.spaceengineers.remoteclient.android.Profile
import com.hiczp.spaceengineers.remoteclient.android.component.ProfileUI
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

class ProfileListAdapter(
    val onDelete: suspend CoroutineScope.(profile: Profile) -> Unit,
    val onOpen: suspend CoroutineScope.(profile: Profile) -> Unit,
    val onModify: suspend CoroutineScope.(profile: Profile) -> Unit
) : RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder>() {
    private var profiles = emptyList<Profile>()

    override fun getItemCount() = profiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileViewHolder(ProfileUI().createView(AnkoContext.create(parent.context, parent)))

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        with(holder) {
            name.text = profile.name
            url.text = profile.url
            delete.onClick {
                onDelete(profile)
            }
            itemView.onClick {
                onOpen(profile)
            }
            itemView.onLongClick {
                onModify(profile)
            }
        }
    }

    fun setProfiles(profiles: List<Profile>) {
        this.profiles = profiles
        notifyDataSetChanged()
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(ProfileUI.nameId)!!
        val url = itemView.findViewById<TextView>(ProfileUI.urlId)!!
        val delete = itemView.findViewById<Button>(ProfileUI.deleteId)!!
    }
}
