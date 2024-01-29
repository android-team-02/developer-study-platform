package com.sesac.developer_study_platform.ui.mystudy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMyStudyBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import kotlinx.coroutines.launch

class MyStudyFragment : Fragment() {

    private var _binding: FragmentMyStudyBinding? = null
    private val binding get() = _binding!!
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            val action = MyStudyFragmentDirections.actionGlobalToDetail(sid)
            findNavController().navigate(action)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setStudyAdapter()
        loadStudyList()
    }

    private fun setStudyAdapter() {
        binding.rvStudyList.adapter = studyAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                studyAdapter.submitList(it.values.toList())
            }.onFailure {
                Log.e("MyStudyFragment", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
