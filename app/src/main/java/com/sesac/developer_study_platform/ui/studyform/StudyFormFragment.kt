package com.sesac.developer_study_platform.ui.studyform

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import com.sesac.developer_study_platform.util.DateFormats
import com.sesac.developer_study_platform.util.formatTimestamp
import com.sesac.developer_study_platform.util.setImage
import com.sesac.developer_study_platform.util.showSnackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyFormFragment : Fragment() {

    private var _binding: FragmentStudyFormBinding? = null
    private val binding get() = _binding!!
    private val dayTimeList = mutableListOf<DayTime>()
    private var totalPeopleCount = ""
    private var language = ""
    private var category = ""
    private var startDate = ""
    private var endDate = ""
    private lateinit var image: Uri
    private val viewModel by viewModels<StudyFormViewModel>()
    private val dayTimeAdapter = DayTimeAdapter(object : DayTimeClickListener {
        override fun onClick(isStartTime: Boolean, dayTime: DayTime) {
            showTimePicker(isStartTime, dayTime)
        }
    })
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            setSelectedImage(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setImageButton()
        with(binding) {
            setCategoryButton(btnAndroid)
            setCategoryButton(btnIos)
            setCategoryButton(btnFrontEnd)
            setCategoryButton(btnBackEnd)
            setCategoryButton(btnAi)
            setCategoryButton(btnEtc)
        }
        validateName()
        validateContent()
        setLanguage()
        binding.tvStartDate.setOnClickListener {
            showDateRangePicker()
        }
        binding.rvDayTime.adapter = dayTimeAdapter
        with(binding) {
            setDayButton(btnMonday)
            setDayButton(btnTuesday)
            setDayButton(btnWednesday)
            setDayButton(btnThursday)
            setDayButton(btnFriday)
            setDayButton(btnSaturday)
            setDayButton(btnSunday)
        }
        setTotalPeopleCount()
        setValidateAll()
        moveToMessageForm()
        setBackButton()
        setNavigation()
    }

    private fun setImageButton() {
        binding.ivImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setSelectedImage(uri: Uri?) {
        if (uri != null) {
            binding.ivImage.setImage(uri.toString())
            binding.groupAddImage.visibility = View.GONE
            image = uri
        }
    }

    private fun setCategoryButton(button: AppCompatButton) {
        val buttonList = binding.flowCategory.referencedIds.map { id ->
            binding.root.findViewById<AppCompatButton>(id)
        }
        button.setOnClickListener {
            button.isSelected = true
            buttonList.forEach {
                if (it != button) {
                    it.isSelected = false
                }
            }
            category = button.text.toString()
        }
    }

    private fun validateName() {
        binding.etStudyNameInput.addTextChangedListener(
            CustomTextWatcher {
                if (it.length == 20) {
                    binding.root.showSnackbar(R.string.study_form_validate_name)
                }
            }
        )
    }

    private fun validateContent() {
        binding.etStudyContentInput.addTextChangedListener(
            CustomTextWatcher {
                if (it.length == 150) {
                    binding.root.showSnackbar(R.string.study_form_validate_content)
                }
            }
        )
    }

    private fun setLanguage() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            resources.getStringArray(R.array.study_form_language)
        )
        binding.tvLanguageDropdown.setAdapter(arrayAdapter)
        binding.tvLanguageDropdown.setOnItemClickListener { parent, _, position, _ ->
            language = parent.getItemAtPosition(position).toString()
        }
    }

    private fun setTotalPeopleCount() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            resources.getStringArray(R.array.study_form_people)
        )
        binding.tvPeopleDropdown.setAdapter(arrayAdapter)
        binding.tvPeopleDropdown.setOnItemClickListener { parent, _, position, _ ->
            totalPeopleCount = parent.getItemAtPosition(position).toString()
        }
    }

    private fun showDateRangePicker() {
        val calendarConstraintBuilder = CalendarConstraints.Builder().apply {
            setValidator(DateValidatorPointForward.now())
        }
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(calendarConstraintBuilder.build())
            .build()
        dateRangePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        setPositiveButton(dateRangePicker)
    }

    private fun setPositiveButton(dateRangePicker: MaterialDatePicker<Pair<Long, Long>>) {
        dateRangePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.first ?: 0
            val selectedStartDate = getDate(calendar)

            calendar.timeInMillis = it.second ?: 0
            val selectedEndDate = getDate(calendar)

            binding.tvStartDate.text = selectedStartDate
            binding.tvEndDate.text = selectedEndDate
            startDate = selectedStartDate
            endDate = selectedEndDate
        }
    }

    private fun getDate(calendar: Calendar): String {
        return SimpleDateFormat(
            DateFormats.YEAR_MONTH_DAY_FORMAT.pattern,
            Locale.getDefault()
        ).format(calendar.time).toString()
    }

    private fun showTimePicker(isStartTime: Boolean, dayTime: DayTime) {
        val timePicker = MaterialTimePicker.Builder()
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        timePicker.show(requireActivity().supportFragmentManager, "TimePicker")
        setPositiveButton(timePicker, isStartTime, dayTime)
    }

    private fun setPositiveButton(
        timePicker: MaterialTimePicker,
        isStartTime: Boolean,
        dayTime: DayTime
    ) {
        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            validateTime(isStartTime, dayTime, selectedTime)
        }
    }

    private fun validateTime(isStartTime: Boolean, dayTime: DayTime, selectedTime: String) {
        val position = dayTimeList.indexOf(dayTime)
        if (isStartTime) {
            val endTime = dayTime.endTime
            if (endTime != null && selectedTime > endTime) {
                binding.root.showSnackbar(R.string.study_form_validate_start_time)
                return
            }
            dayTime.startTime = selectedTime
        } else {
            val startTime = dayTime.startTime
            if (startTime != null && selectedTime < startTime) {
                binding.root.showSnackbar(R.string.study_form_validate_end_time)
                return
            }
            dayTime.endTime = selectedTime
        }
        if (position != -1) {
            dayTimeAdapter.notifyItemChanged(position)
        }
    }

    private fun setDayButton(button: AppCompatButton) {
        button.setOnClickListener {
            button.isSelected = !button.isSelected
            addScheduleForDay(button.text.toString())
        }
    }

    private fun addScheduleForDay(day: String) {
        val index = dayTimeList.indexOfFirst { it.day == day }
        if (index == -1) {
            dayTimeList.add(DayTime(day))
        } else {
            dayTimeList.removeAt(index)
        }
        dayTimeAdapter.submitList(dayTimeList.sortedBy { getDaySort(it.day) }.toList())
    }

    private fun getDaySort(day: String): Int {
        return when (day) {
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            else -> 7
        }
    }

    private fun setValidateAll() {
        binding.btnCreateStudy.setOnClickListener {
            when {
                binding.ivImage.drawable == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_image)
                }

                category.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_category)
                }

                binding.etStudyNameInput.text.toString().isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_name_input)
                }

                binding.etStudyContentInput.text.toString().isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_content_input)
                }

                language.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_language)
                }

                startDate.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_start_date)
                }

                endDate.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_end_date)
                }

                dayTimeList.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_day)
                }

                dayTimeList.any { it.startTime == null } -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_start_time)
                }

                dayTimeList.any { it.endTime == null } -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_end_time)
                }

                totalPeopleCount.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_people)
                }

                else -> {
                    val uid = Firebase.auth.uid
                    uid?.let {
                        val sid = "@make@$uid@time@${formatTimestamp()}"
                        uploadImage(sid, image) { fileName ->
                            saveStudy(sid, formatStudy(sid, uid, fileName))
                            saveUserStudy(uid, sid, formatUserStudy(sid, fileName))
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(sid: String, image: Uri, onUploadSuccess: (String) -> Unit) {
        val fileName = "image_${binding.etStudyNameInput.text}.jpg"
        viewModel.uploadImage(sid, fileName, image, onUploadSuccess)
    }

    private fun saveStudy(sid: String, study: Study) {
        viewModel.saveStudy(sid, study)
    }

    private fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        viewModel.saveUserStudy(uid, sid, userStudy)
    }

    private fun formatStudy(sid: String, uid: String, fileName: String): Study {
        return Study(
            sid = sid,
            name = binding.etStudyNameInput.text.toString(),
            image = fileName,
            content = binding.etStudyContentInput.text.toString(),
            category = category,
            language = language,
            totalMemberCount = totalPeopleCount.toInt(),
            days = formatDayTimeList(),
            startDate = binding.tvStartDate.text.toString(),
            endDate = binding.tvEndDate.text.toString(),
            members = mapOf(uid to true),
            banUsers = mapOf("default" to true)
        )
    }

    private fun formatUserStudy(sid: String, fileName: String): UserStudy {
        return UserStudy(
            sid = sid,
            name = binding.etStudyNameInput.text.toString(),
            image = fileName,
            language = language,
            days = formatDayTimeList(),
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun formatDayTimeList(): List<String> {
        val list = mutableListOf<String>()
        dayTimeList.forEach {
            list.add("${it.day} ${it.startTime}~${it.endTime}")
        }
        return list
    }

    private fun moveToMessageForm() {
        viewModel.moveToMessage()
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun setNavigation() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                //채팅화면으로 이동
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
