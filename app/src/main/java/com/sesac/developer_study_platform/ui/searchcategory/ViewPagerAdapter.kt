package com.sesac.developer_study_platform.ui.searchcategory

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.ui.category.CategoryFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return CategoryFragment.create(Category.entries[position])
    }

    override fun getItemCount(): Int {
        return Category.entries.size
    }
}