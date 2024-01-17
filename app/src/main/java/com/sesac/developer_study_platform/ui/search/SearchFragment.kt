package com.sesac.developer_study_platform.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentSearchBinding
import com.sesac.developer_study_platform.ui.home.HomeFragmentDirections

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
        binding.sbSearch.setOnClickListener {
            findNavController().navigate(R.id.dest_search_result)
        }
        with(binding) {
            setCategoryButton(tvAndroid)
            setCategoryButton(tvIos)
            setCategoryButton(tvFrontEnd)
            setCategoryButton(tvBackEnd)
            setCategoryButton(tvAi)
            setCategoryButton(tvEtc)
        }
    }

    private fun setCategoryButton(view: TextView) {
        view.setOnClickListener {
            val action = HomeFragmentDirections.actionGlobalToSearchCategory(
                getPosition(view.text.toString())
            )
            findNavController().navigate(action)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}