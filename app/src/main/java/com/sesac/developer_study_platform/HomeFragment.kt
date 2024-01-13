package com.sesac.developer_study_platform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}