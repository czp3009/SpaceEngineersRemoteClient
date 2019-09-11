package com.hiczp.spaceengineers.remoteclient.android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragmentAndTags: Array<Pair<() -> Fragment, String>>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount() = fragmentAndTags.size

    override fun getItem(position: Int) = fragmentAndTags[position].first()

    override fun getPageTitle(position: Int) = fragmentAndTags[position].second
}
