package com.sesac.developer_study_platform.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentHomeBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            // TODO 채팅 화면으로 이동
        }
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

        setStudyAdapter()
        setDetailButton()
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

    private fun setStudyAdapter() {
        binding.rvStudyList.adapter = studyAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun setDetailButton() {
        binding.tvDetail.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToMyStudy()
            findNavController().navigate(action)
        }
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                studyAdapter.submitList(it.values.toList())
            }.onFailure {
                binding.groupStudyForm.visibility = View.VISIBLE
                binding.rvStudyList.visibility = View.GONE
                Log.e("HomeFragment", it.message ?: "error occurred.")
            }
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