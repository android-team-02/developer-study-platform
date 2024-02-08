package com.sesac.developer_study_platform.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentMyPageBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MyPageViewModel>()
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            viewModel.moveToMessage()
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        setDaysDotSpan()
        updateSelectedDayStudyList()
        setBookmarkButton()
        setDialogButton()
        setNavigation()
    }

    private fun setStudyAdapter() {
        binding.rvMyStudyList.adapter = studyAdapter
        binding.rvMyStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadUser() {
        lifecycleScope.launch {
            viewModel.loadUser()
        }
        viewModel.studyUserEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.studyUser = it
            }
        )
    }

    private fun loadStudyList() {
        lifecycleScope.launch {
            viewModel.loadStudyList()
        }
    }

    private fun setDaysDotSpan() {
        viewModel.dotSpanAllDays.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.mcv.addDecorators(DotSpanDecorator(it))
            })
    }

    private fun updateSelectedDayStudyList() {
        binding.mcv.setOnDateChangedListener { _, date, _ ->
            viewModel.getStudyList(date)
        }
        viewModel.selectedDayStudy.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.groupMyStudy.visibility = View.VISIBLE
                studyAdapter.submitList(it.toList())
            })
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
        viewModel.moveToBookmarkEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_my_to_bookmark)
            }
        )
        viewModel.moveToDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                //로그아웃 다이얼로그로 이동
            }
        )
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                //채팅방으로 이동
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}