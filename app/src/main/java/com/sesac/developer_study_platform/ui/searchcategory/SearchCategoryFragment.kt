package com.sesac.developer_study_platform.ui.searchcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentSearchCategoryBinding
import com.sesac.developer_study_platform.util.isNetworkConnected

class SearchCategoryFragment : Fragment() {

    private var _binding: FragmentSearchCategoryBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SearchCategoryFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        binding.tl.post {
            binding.tl.getTabAt(args.position)?.select()
        }
        binding.isNetworkConnected = isNetworkConnected(requireContext())
    }

    private fun setViewPager() {
        binding.vp.adapter = ViewPagerAdapter(this)
        attachTabLayout()
    }

    private fun attachTabLayout() {
        TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
            tab.text = getString(Category.entries[position].resId)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}