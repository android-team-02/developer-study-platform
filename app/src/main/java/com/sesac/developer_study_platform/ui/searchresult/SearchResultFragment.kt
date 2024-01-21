package com.sesac.developer_study_platform.ui.searchresult

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentSearchResultBinding
import com.sesac.developer_study_platform.ui.SearchClickListener
import kotlinx.coroutines.launch

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val searchAdapter = SearchAdapter(object : SearchClickListener {
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

        setSearchView()
        setBackBtn()
        binding.rvStudyList.adapter = searchAdapter
        searchStudy()
    }

    private fun setSearchView() {
        with(binding.etSearch) {
            this.requestFocus()
            showKeyboard(this)
        }
    }

    private fun showKeyboard(textInputEditText: TextInputEditText) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(textInputEditText, 0)
    }

    private fun setBackBtn() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun searchStudy() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.d("SearchResultFragment1", p0.toString())
                if (p0 != null) {
                    if (p0.isEmpty()) {
                        searchAdapter.submitList(emptyList())
                    } else {
                        loadSearchStudyList(p0.toString())
                    }
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
                Log.d("SearchResultFragment", it.toString())
            }.onFailure {
                Log.e("SearchResultFragment", it.message ?: "error occurred.")
            }
        }
    }

    private fun setSearchStudyList(searchStudyMap: Map<String, Study>) {
        val searchStudyList = searchStudyMap.values.toList().sortedBy {
            val comparator = it.totalMemberCount - it.members.count()
            if (comparator != 0) {
                it.totalMemberCount - it.members.count()
            } else {
                8
            }
        }
        searchAdapter.submitList(searchStudyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}