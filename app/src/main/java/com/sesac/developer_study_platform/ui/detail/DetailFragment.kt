package com.sesac.developer_study_platform.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkDao
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private var currentStudy: Study? = null
    private lateinit var bookmarkStudy: Study

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studySid = args.studyId
        fetchStudyDetails(studySid)

        binding.toolbarArrowDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        loadBookmarkButtonState()
        setBookmarkButton()
    }

    private fun fetchStudyDetails(studyId: String) {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getDetail("@make@abcd@time@20240111144250")
            }.onSuccess { studies ->
                bookmarkStudy = studies
                displayStudyDetails(studies)
            }.onFailure { exception ->
                Log.e("DetailFragment2", "Failed to load study details: ${exception.message}")
            }
        }
    }

    private fun displayStudyDetails(study: Study) {
        binding.tvStudyName.text = study.name
        binding.tvStudyContent.text = study.content
        binding.tvCategory.text = study.category
        binding.tvLanguage.text = study.language
        val currentMemberCount = study.members.keys.size
        binding.tvPeople.text =
            getString(R.string.all_study_people, currentMemberCount, study.totalMemberCount)
        val studyTime = study.days.entries.joinToString("\n") { (day, time) ->
            val parts = time.split("@")
            if (parts.size == 2) {
                val dayOfWeek = when (day) {
                    "월" -> "월요일"
                    "화" -> "화요일"
                    "수" -> "수요일"
                    "목" -> "목요일"
                    "금" -> "금요일"
                    "토" -> "토요일"
                    "일" -> "일요일"
                    else -> day
                }
                val startTime = formatTime(parts[0])
                val endTime = formatTime(parts[1])
                "$dayOfWeek $startTime ~ $endTime"
            } else {
                "$day $time"
            }
        }

        binding.tvTime.text = getString(R.string.study_time_format, studyTime)
        binding.tvPeriod.text =
            getString(R.string.study_period_format, study.startDate, study.endDate)

        fetchStudyParticipants(study.members.keys)
    }

    private fun fetchStudyParticipants(uids: Set<String>) {
        val service = StudyService.create()
        val participantNames = mutableListOf<String>()

        uids.forEach { uid ->
            lifecycleScope.launch {
                runCatching {
                    service.getUserById(uid)
                }.onSuccess { user ->
                    participantNames.add(user.userId)
                    if (participantNames.size == uids.size) {
                        updateParticipantsUI(participantNames)
                    }
                    joinStudy()
                }.onFailure { exception ->
                    Log.e("DetailFragment", "Failed to load user details: ${exception.message}")
                }
            }
        }
    }

    private fun updateParticipantsUI(participantNames: List<String>) {
        val participantsText = participantNames.joinToString("\n")
        binding.tvParticipants.text = participantsText
    }

    private fun joinStudy() {
        currentStudy?.let { study ->
            binding.btnJoinStudy.isEnabled = !(isDeadline(study) || isMemberLimit(study))
        }
    }

    private fun isDeadline(study: Study): Boolean {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val currentDate = dateFormat.format(today.time).replace("/", "")
        val formattedEndDate = study.endDate.replace("/", "")
        return currentDate > formattedEndDate
    }

    private fun isMemberLimit(study: Study): Boolean {
        return study.members.keys.size >= study.totalMemberCount
    }

    /* private fun isUserBanned(study: Study): Boolean {
         val currentUser = getCurrentUser()
         return currentUser in study.banUsers.keys
     } */

    private fun formatTime(time: String): String {
        val hour = time.substring(0, 2)
        val minute = time.substring(2)
        return "$hour:$minute"
    }

    private fun loadBookmarkButtonState() {
        lifecycleScope.launch {
            binding.ivBookmark.isSelected =
                bookmarkDao.getBookmarkStudyBySid("@make@abcd@time@20240111144250").isNotEmpty()
        }
    }

    private fun setBookmarkButton() {
        binding.ivBookmark.setOnClickListener {
            if (binding.ivBookmark.isSelected) {
                binding.ivBookmark.isSelected = false
                deleteBookmarkStudyBySid()
            } else {
                binding.ivBookmark.isSelected = true
                insertBookmarkStudy()
            }
        }
    }

    private fun insertBookmarkStudy() {
        lifecycleScope.launch {
            bookmarkDao.insertBookmarkStudy(
                BookmarkStudy(
                    bookmarkStudy.sid,
                    bookmarkStudy.name,
                    bookmarkStudy.image,
                    bookmarkStudy.language,
                    bookmarkStudy.days.keys.joinToString(", ")
                )
            )
        }
    }

    private fun deleteBookmarkStudyBySid() {
        lifecycleScope.launch {
            bookmarkDao.deleteBookmarkStudyBySid("@make@abcd@time@20240111144250")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
