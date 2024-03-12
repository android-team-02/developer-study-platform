package com.sesac.developer_study_platform.ui.mypage

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
import com.sesac.developer_study_platform.databinding.FragmentMyPageBinding
import com.sesac.developer_study_platform.util.isNetworkConnected
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MyPageViewModel>()
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mcv.addDecorators(TodayDecorator())
        setStudyAdapter()
        loadUser()
        loadStudyList()
        setDotSpanDayList()
        setSelectedDayEmpty()
        setSelectedDayStudyList()
        setBookmarkButton()
        setDialogButton()
        setNavigation()
        binding.isNetworkConnected = isNetworkConnected(requireContext())
    }

    private fun setStudyAdapter() {
        binding.rvMyStudyList.adapter = studyAdapter
        binding.rvMyStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadUser() {
        viewModel.loadUser()
        viewModel.studyUserEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.studyUser = it
            }
        )
    }

    private fun loadStudyList() {
        viewModel.myStudyList.observe(viewLifecycleOwner) {
            viewModel.setDotSpanDayList(it)
            viewModel.studyList = it
        }
    }

    private fun setDotSpanDayList() {
        viewModel.dotSpanDayListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.mcv.addDecorators(DotSpanDecorator(it))
            }
        )
    }

    private fun setSelectedDayEmpty() {
        viewModel.isSelectedDayEmpty.observe(viewLifecycleOwner) {
            binding.isSelectedDayEmpty = it
        }
    }

    private fun setSelectedDayStudyList() {
        binding.mcv.setOnDateChangedListener { _, date, _ ->
            viewModel.setSelectedDayStudyList(date)
        }
        viewModel.selectedDayStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                studyAdapter.submitList(it)
                binding.isSelectedDayEmpty = it.isEmpty()
            }
        )
    }

    private fun setBookmarkButton() {
        binding.ivBookmark.setOnClickListener {
            viewModel.moveToBookmark()
        }
    }

    private fun setDialogButton() {
        binding.tvLogout.setOnClickListener {
            viewModel.moveToDialog()
        }
    }

    private fun setNavigation() {
        moveToBookmark()
        moveToDialog()
        moveToMessage()
    }

    private fun moveToBookmark() {
        viewModel.moveToBookmarkEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_my_to_bookmark)
            }
        )
    }

    private fun moveToDialog() {
        viewModel.moveToDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_my_to_logout_dialog)
            }
        )
    }

    private fun moveToMessage() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = MyPageFragmentDirections.actionGlobalToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}