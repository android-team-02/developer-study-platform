package com.sesac.developer_study_platform.ui.searchresult

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentSearchResultBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import kotlinx.coroutines.launch

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val searchAdapter = SearchAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            val action = SearchResultFragmentDirections.actionGlobalToDetail(sid)
            findNavController().navigate(action)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSoftKeyboard(binding.etSearch)
        setBackButton()
        setSearchAdapter()
        searchStudy()
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = context?.getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(view, SHOW_IMPLICIT)
        }
    }

    private fun setBackButton() {
        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setSearchAdapter() {
        binding.rvStudyList.adapter = searchAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun searchStudy() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    searchAdapter.submitList(emptyList())
                } else {
                    loadSearchStudyList(s.toString())
                }
            }
        })
    }

    private fun loadSearchStudyList(searchKeyword: String) {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getSearchStudyList("\"${searchKeyword}\"", "\"${searchKeyword}\\uf8ff\"")
            }.onSuccess {
                setSearchStudyList(it)
            }.onFailure {
                Log.e("SearchResultFragment", it.message ?: "error occurred.")
            }
        }
    }

    private fun setSearchStudyList(searchStudyMap: Map<String, Study>) {
        val searchStudyList = searchStudyMap.values.toList().sortedBy {
            val comparator = it.totalMemberCount - it.members.count()
            if (comparator != 0) {
                comparator
            } else {
                MAX_MEMBER_COUNT
            }
        }
        searchAdapter.submitList(searchStudyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MAX_MEMBER_COUNT = 8
    }
}