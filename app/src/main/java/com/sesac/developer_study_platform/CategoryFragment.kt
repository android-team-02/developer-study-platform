package com.sesac.developer_study_platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sesac.developer_study_platform.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: String

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