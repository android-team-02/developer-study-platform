package com.sesac.developer_study_platform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studyId = args.studyId
        fetchStudyDetails(studyId)

        binding.ivArrowDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivJoinStudy.setOnClickListener {
            joinStudy(studyId)
        }
    }
    private fun fetchStudyDetails(studyId: String) {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getStudyDetail(studyId)
            }.onSuccess { study ->
                displayStudyDetails(study)
            }.onFailure { exception ->
                Log.e("DetailFragment", "Failed to load study details: ${exception.message}")
            }
        }
    }
    private fun displayStudyDetails(study: Study) {
        binding.tvStudyName.text = study.name
        binding.tvStudyContent.text = study.content
        binding.tvCategory.text = study.category
        binding.tvLanguage.text = study.language
        binding.tvPeople.text = getString(R.string.study_people_format, study.currentMemberCount, study.totalMemberCount)
        val studyTime = study.days.joinToString(", ") { day ->
            val parts = day.split("@")
            if (parts.size == 3) {
                val dayOfWeek = when (parts[0]) {
                    "월" -> "월요일"
                    "화" -> "화요일"
                    "수" -> "수요일"
                    "목" -> "목요일"
                    "금" -> "금요일"
                    "토" -> "토요일"
                    "일" -> "일요일"
                    else -> parts[0]
                }
                val startTime = formatTime(parts[1])
                val endTime = formatTime(parts[2])
                "$dayOfWeek $startTime ~ $endTime"
            } else {
                day
            }
        }

        binding.tvTime.text = getString(R.string.study_time_format, studyTime)
        binding.tvPeriod.text = getString(R.string.study_period_format, study.startDate, study.endDate)
        //참여자 목록을 보여 주는 데이터 필요함
    }

    private fun joinStudy(studyId: String) {
        // 사용자가 '스터디 참여하기' 버튼을 클릭 시 동작 구현
    }

    private fun formatTime(time: String): String {
        val hour = time.substring(0, 2)
        val minute = time.substring(2)
        return "$hour:$minute"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
