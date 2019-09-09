package com.hiczp.spaceengineers.remoteclient.android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hiczp.spaceengineers.remoteclient.android.fragment.ChatFragment

class VRageFragmentPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentAndTags = arrayOf(
        { ChatFragment() } to "Chat"
        //{ PlayerFragment() } to "Player"
    )

    override fun getCount() = fragmentAndTags.size

    override fun getItem(position: Int): Fragment {
        val (fragment, tag) = fragmentAndTags[position]
        return fragment()
    }

    override fun getPageTitle(position: Int) = fragmentAndTags[position].second
}
