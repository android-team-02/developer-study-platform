package com.sesac.developer_study_platform.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentSearchBinding
import com.sesac.developer_study_platform.isNetworkConnected
import com.sesac.developer_study_platform.ui.home.HomeFragmentDirections

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchBar()
        with(binding) {
            setCategoryButton(tvAndroid)
            setCategoryButton(tvIos)
            setCategoryButton(tvFrontEnd)
            setCategoryButton(tvBackEnd)
            setCategoryButton(tvAi)
            setCategoryButton(tvEtc)
        }
        setNavigation()
        networkStatus()
    }

    private fun setSearchBar() {
        binding.sbSearch.setOnClickListener {
            viewModel.moveToSearchResult()
        }
    }

    private fun setCategoryButton(view: TextView) {
        view.setOnClickListener {
            viewModel.moveToCategory(view.text.toString())
        }
    }

    private fun setNavigation() {
        viewModel.moveToSearchResultEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_search_to_search_result)
            }
        )
        viewModel.moveToCategoryEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = HomeFragmentDirections.actionGlobalToSearchCategory(
                    getPosition(it)
                )
                findNavController().navigate(action)
            }
        )
    }

    private fun getPosition(category: String): Int {
        return if (category == getString(R.string.all_etc)) {
            Category.ETC.ordinal
        } else {
            Category.valueOf(
                category.replace("-", "").uppercase()
            ).ordinal
        }
    }

    private fun networkStatus() {
        if (!isNetworkConnected(requireContext())) {
            binding.networkStatus.visibility = View.VISIBLE
        } else {
            binding.networkStatus.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}