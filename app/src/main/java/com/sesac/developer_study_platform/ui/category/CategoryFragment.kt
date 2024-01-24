package com.sesac.developer_study_platform.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentCategoryBinding
import com.sesac.developer_study_platform.ui.common.GridSpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.sortStudyList
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: String
    private val categoryAdapter = CategoryAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            val action = CategoryFragmentDirections.actionGlobalToDetail(sid)
            findNavController().navigate(action)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryResId = arguments?.getInt(CATEGORY_RES_ID)
        if (categoryResId != null) {
            category = getString(categoryResId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCategoryAdapter()
        loadStudyList()
    }

    private fun setCategoryAdapter() {
        binding.rvStudyList.adapter = categoryAdapter
        binding.rvStudyList.addItemDecoration(
            GridSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_median))
        )
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getStudyList("\"${category}\"")
            }.onSuccess {
                val studyList = it.values.toList().sortStudyList()
                categoryAdapter.submitList(studyList)
            }.onFailure {
                Log.e("CategoryFragment", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CATEGORY_RES_ID = "category_res_id"

        fun create(category: Category) = CategoryFragment().apply {
            arguments = Bundle().apply {
                putInt(CATEGORY_RES_ID, category.resId)
            }
        }
    }
}