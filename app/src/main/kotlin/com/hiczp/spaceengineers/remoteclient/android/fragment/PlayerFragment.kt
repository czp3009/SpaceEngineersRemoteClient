package com.hiczp.spaceengineers.remoteclient.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hiczp.spaceengineers.remoteclient.android.adapter.TabFragmentPagerAdapter
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import org.jetbrains.anko.verticalLayout

class PlayerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager
        val view = UI {
            verticalLayout {
                tabLayout = tabLayout()
                viewPager = viewPager {
                    id = pagerViewId
                    adapter = TabFragmentPagerAdapter(requireFragmentManager(), arrayOf(
                        { PlayersFragment() } to "Players",
                        { CharactersFragment() } to "Characters"
                    ))
                }
            }
        }.view
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    companion object {
        private val pagerViewId = View.generateViewId()
    }
}
