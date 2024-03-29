package com.sesac.developer_study_platform.ui.mystudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentMyStudyBinding
import com.sesac.developer_study_platform.util.isNetworkConnected
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class MyStudyFragment : Fragment() {

    private var _binding: FragmentMyStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MyStudyViewModel>()
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            viewModel.moveToMessage(sid)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_study, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackButton()
        setStudyAdapter()
        loadStudyList()
        setNavigation()
        binding.isNetworkConnected = isNetworkConnected(requireContext())
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun setStudyAdapter() {
        binding.rvStudyList.adapter = studyAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        viewModel.myStudyList.observe(viewLifecycleOwner) {
            studyAdapter.submitList(it)
        }
    }

    private fun setNavigation() {
        moveToBack()
        moveToMessage()
    }

    private fun moveToBack() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
    }

    private fun moveToMessage() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = MyStudyFragmentDirections.actionGlobalToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
