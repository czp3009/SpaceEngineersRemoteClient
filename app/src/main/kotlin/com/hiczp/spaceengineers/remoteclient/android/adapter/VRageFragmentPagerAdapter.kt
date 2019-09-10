package com.hiczp.spaceengineers.remoteclient.android.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hiczp.spaceengineers.remoteclient.android.fragment.ChatFragment
import com.hiczp.spaceengineers.remoteclient.android.fragment.PlayerFragment

class VRageFragmentPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentAndTags = arrayOf(
        { ChatFragment() } to "Chat",
        { PlayerFragment() } to "Player"
    )

    override fun getCount() = fragmentAndTags.size

    override fun getItem(position: Int) = fragmentAndTags[position].first()

    override fun getPageTitle(position: Int) = fragmentAndTags[position].second
}
