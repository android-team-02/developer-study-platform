package com.sesac.developer_study_platform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sesac.developer_study_platform.databinding.FragmentJoinStudyListBinding
import kotlinx.coroutines.launch

class JoinStudyListFragment : Fragment() {

    private var _binding: FragmentJoinStudyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var joinStudyAdapter: JoinStudyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinStudyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        joinStudyAdapter = JoinStudyAdapter { _ ->
            val action = JoinStudyListFragmentDirections
                .actionJoinStudyListFragmentToDetailFragment()
            findNavController().navigate(action)
        }

        binding.rvStudy.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = joinStudyAdapter
        }

        loadStudyList()
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getStudyList()
            }.onSuccess { studies ->
                joinStudyAdapter.submitList(studies)
            }.onFailure { exception ->
                Log.e("JoinStudyListFragment", "Failed to load studies: ${exception.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
