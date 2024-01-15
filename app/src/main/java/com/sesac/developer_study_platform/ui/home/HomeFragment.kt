package com.sesac.developer_study_platform.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.ui.SpaceItemDecoration
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.ui.StudyClickListener
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(study: Study) {}
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvStudyList.adapter = studyAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
        loadStudyList()
        with(binding) {
            setCategoryButton(tvAndroid)
            setCategoryButton(tvIos)
            setCategoryButton(tvFrontEnd)
            setCategoryButton(tvBackEnd)
            setCategoryButton(tvAi)
            setCategoryButton(tvEtc)
        }
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getStudyList()
            }.onSuccess {
                setStudyList(it)
            }.onFailure {
                Log.e("HomeFragment", it.message ?: "error occurred.")
            }
        }
    }

    private fun setStudyList(studyList: List<Study>?) {
        binding.rvStudyList.isVisible = !studyList.isNullOrEmpty()
        binding.groupStudyForm.isVisible = studyList.isNullOrEmpty()
        studyAdapter.submitList(studyList)
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