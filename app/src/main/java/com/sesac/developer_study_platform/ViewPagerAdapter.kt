package com.sesac.developer_study_platform

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return CategoryFragment.create(Category.entries[position])
    }

    override fun getItemCount(): Int {
        return Category.entries.size
    }
}