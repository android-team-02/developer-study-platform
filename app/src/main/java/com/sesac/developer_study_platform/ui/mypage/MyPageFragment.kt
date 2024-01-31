package com.sesac.developer_study_platform.ui.mypage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMyPageBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.formatCalendarDate
import com.sesac.developer_study_platform.util.setImage
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val uid = Firebase.auth.uid
    private val studyList = mutableListOf<UserStudy>()
    private val calendar = Calendar.getInstance()
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            //채팅방으로 이동하기
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogout.setOnClickListener {
            //로그아웃 다이얼로그로 이동하기
        }
        binding.ivBookmark.setOnClickListener {
            findNavController().navigate(R.id.action_my_to_bookmark)
        }
        setStudyAdapter()
        loadUser()
        loadStudyList()
    }

    private fun setStudyAdapter() {
        binding.rvMyStudyList.adapter = studyAdapter
        binding.rvMyStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadUser() {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                uid?.let {
                    studyService.getUserById(uid)
                }
            }.onSuccess {
                if (it != null) {
                    binding.tvProfileName.text = it.userId
                    binding.ivProfileImage.setImage(it.image)
                }
            }.onFailure {
                Log.e("MyPageFragment-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getUserStudyList(uid)
            }.onSuccess {
                studyList.addAll(it.values)
                setDaysDotSpan()
            }.onFailure {
                Log.e("MyPageFragment", it.message ?: "error occurred.")
            }
        }
    }

    private fun setDaysDotSpan() {
        studyList.forEach {
            val days = getDotSpanDays(
                it.startDate.formatCalendarDate(),
                it.endDate.formatCalendarDate(),
                formatDays(it.days)
            )
            binding.mcv.addDecorators(DotSpanDecorator(Color.BLACK, days))
        }
    }

    private fun getDotSpanDays(startDate: Date, endDate: Date, days: List<String>): List<CalendarDay> {
        val dotSpanDay = ArrayList<CalendarDay>()
        calendar.time = startDate
        while (calendar.time <= endDate) {
            val calendarDays = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
            if (days.contains(calendarDays)) {
                dotSpanDay.add(
                    CalendarDay.from(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                )
            }
            calendar.add(Calendar.DATE, 1)
        }
        return dotSpanDay
    }

    private fun formatDays(days: List<String>): List<String> {
        return days.map { it.substringBefore(" ") }.toList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}