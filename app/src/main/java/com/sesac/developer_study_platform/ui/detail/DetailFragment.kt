package com.sesac.developer_study_platform.ui.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Day
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkDao
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentDetailBinding
import com.sesac.developer_study_platform.util.formatDate
import com.sesac.developer_study_platform.util.getAllDayList
import com.sesac.developer_study_platform.util.getDayList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var study: Study

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        loadStudy()
        loadBookmarkButtonState()
        setBookmarkButton()
    }

    private fun loadStudy() {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getStudy(args.studyId)
            }.onSuccess {
                study = it
                setStudy()
                setJoinStudyButton()
            }.onFailure {
                Log.e("DetailFragment-loadStudy", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun setStudy() {
        binding.tvStudyName.text = study.name
        binding.tvStudyContent.text = study.content
        binding.tvCategoryValue.text = study.category
        binding.tvLanguageValue.text = study.language
        binding.tvPeopleValue.text = getString(
            R.string.all_study_people_format,
            study.members.count(),
            study.totalMemberCount
        )
        binding.tvTimeValue.text = getDayTimeList()
        binding.tvPeriodValue.text =
            getString(R.string.detail_study_period_format, study.startDate, study.endDate)
        binding.tvMemberValue.text = getStudyMemberList(study.members.keys).joinToString("\n")
    }

    private fun getDayTimeList(): String {
        val dayTimeList = mutableMapOf<Int, String>()
        study.days.entries.forEach {
            Day.entries.forEach { day ->
                if (getString(day.resId) == it.key) {
                    dayTimeList[day.ordinal] = formatDayTime(it.key, it.value)
                }
            }
        }
        return dayTimeList.toSortedMap().values.joinToString("\n")
    }

    private fun formatDayTime(day: String, time: String): String {
        val startTime = formatTime(time.split("@").first())
        val endTime = formatTime(time.split("@").last())
        return getString(R.string.detail_study_day_time_format, day, startTime, endTime)
    }

    private fun formatTime(time: String): String {
        return getString(R.string.detail_study_time_format, time.take(2), time.takeLast(2))
    }

    private suspend fun getStudyMemberList(uidList: Set<String>): List<String> {
        val service = StudyService.create()
        return lifecycleScope.async {
            val memberList = mutableListOf<String>()
            uidList.forEach {
                runCatching {
                    service.getUserById(it)
                }.onSuccess {
                    memberList.add(it.userId)
                }.onFailure {
                    Log.e("DetailFragment-getStudyMemberList", it.message ?: "error occurred.")
                }
            }
            memberList
        }.await()
    }

    private fun setJoinStudyButton() {
        val isExpire = getToday().formatDate().toInt() > study.endDate.replace("/", "").toInt()
        val hasFullMember = study.members.count() == study.totalMemberCount
        val isBanUser = study.banUsers.containsKey(Firebase.auth.uid)
        binding.btnJoinStudy.isEnabled = !(isExpire || hasFullMember || isBanUser)
    }

    private fun getToday(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            Date().toString()
        }
    }

    private fun loadBookmarkButtonState() {
        lifecycleScope.launch {
            binding.ivBookmark.isSelected =
                bookmarkDao.getBookmarkStudyBySid(args.studyId).isNotEmpty()
        }
    }

    private fun setBookmarkButton() {
        binding.ivBookmark.setOnClickListener {
            if (binding.ivBookmark.isSelected) {
                binding.ivBookmark.isSelected = false
                deleteBookmarkStudyBySid()
            } else {
                binding.ivBookmark.isSelected = true
                insertBookmarkStudy(it)
            }
        }
    }

    private fun insertBookmarkStudy(view: View) {
        lifecycleScope.launch {
            bookmarkDao.insertBookmarkStudy(
                BookmarkStudy(
                    study.sid,
                    study.name,
                    study.image,
                    study.language,
                    study.days.keys.getDayList(view.getAllDayList())
                )
            )
        }
    }

    private fun deleteBookmarkStudyBySid() {
        lifecycleScope.launch {
            bookmarkDao.deleteBookmarkStudyBySid(args.studyId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}