package com.sesac.developer_study_platform.ui.searchresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentSearchResultBinding
import com.sesac.developer_study_platform.util.isNetworkConnected
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.ui.studyform.CustomTextWatcher

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchResultViewModel>()
    private val searchAdapter = SearchAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            viewModel.moveToDetail(sid)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSoftKeyboard(binding.etSearch)
        setBackButton()
        setNavigation()
        setSearchAdapter()
        searchStudy()
        setSearchStudyList()
        binding.isNetworkConnected = isNetworkConnected(requireContext())
    }

    private fun showSoftKeyboard(view: TextInputEditText) {
        if (view.requestFocus()) {
            val imm = context?.getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(view, SHOW_IMPLICIT)
        }
    }

    private fun setBackButton() {
        binding.ivArrowBack.setOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun setNavigation() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
        viewModel.moveToDetailEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = SearchResultFragmentDirections.actionGlobalToDetail(it)
                findNavController().navigate(action)
            }
        )
    }

    private fun setSearchAdapter() {
        binding.rvStudyList.adapter = searchAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun searchStudy() {
        binding.etSearch.addTextChangedListener(
            CustomTextWatcher {
                if (it.isEmpty()) {
                    searchAdapter.submitList(emptyList())
                } else {
                    viewModel.loadStudyList(it)
                }
            }
        )
    }

    private fun setSearchStudyList() {
        viewModel.searchStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                searchAdapter.submitList(it)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}