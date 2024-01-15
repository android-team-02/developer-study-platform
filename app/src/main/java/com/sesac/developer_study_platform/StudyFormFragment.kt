package com.sesac.developer_study_platform

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class StudyFormFragment : Fragment() {

    private var _binding: FragmentStudyFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var dayTimeAdapter: DayTimeAdapter
    private val dayTimeList = mutableListOf<DayTime>()
    private var totalSelectedItem = ""
    private var languageSelectedItem = ""
    private var categorySelectedItem = ""
    private var startDate: Date? = null
    private var endDate: Date? = null
    private val dayTimeClickListener = object : DayTimeClickListener {
        override fun onClick(dayTime: DayTime, isStartTime: Boolean) {
            setStartTimePicker(isStartTime, dayTime)
        }
    }
    private val imageSelect = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? -> setSelectedImage(uri) }

    private val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
    { uri: Uri? -> setSelectedImage(uri) }

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

        setImageBtn()
        setCategory()
        setValidateText()
        setLanguageSelected()
        setStartDatePicker()
        setEndDatePicker()
        setDayTimeAdapter()
        setDaySelected()
        setTotalPeopleSelected()
        setValidateAll()
        setValidateText()

    }

    private fun setImageBtn() {
        binding.ivImageInput.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                imageSelect.launch("image/*")
            } else {
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun setSelectedImage(uri: Uri?) {
        if (uri != null) {
            binding.ivImageInput.setImageURI(uri)
            binding.groupAddImage.visibility = View.GONE
        } else {
            Log.d("SelectedImage", "No media selected")
        }
    }

    private fun setCategory() {
        val buttons = binding.flowCategory.referencedIds.map { id ->
            binding.root.findViewById<AppCompatButton>(id)
        }

        val categoryClickListener = View.OnClickListener { clickedView ->
            buttons.forEach { it.isSelected = false }
            val clickedButton = clickedView as AppCompatButton
            clickedButton.isSelected = true
            categorySelectedItem = clickedButton.text.toString()
        }

        buttons.forEach { it.setOnClickListener(categoryClickListener) }
    }

    private fun setValidateText() {
        val name = binding.etStudyNameInput
        val content = binding.etStudyContentInput

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateName(name)
                validateContent(content)
            }
        }
        name.addTextChangedListener(textWatcher)
        content.addTextChangedListener(textWatcher)
    }

    private fun validateContent(content: EditText) {
        val maxLines = 4
        if (content.lineCount > maxLines) {
            val lines = content.text.toString().lines().take(maxLines)
            val newText = lines.joinToString("\n")
            content.setText(newText)
            content.setSelection(newText.length)
            R.string.study_form_validate_content.showSnackbar(binding.clStudyForm)
        }
    }

    private fun validateName(name: EditText) {
        if (name.text.toString().length == 20) {
            R.string.study_form_validate_name.showSnackbar(binding.clStudyForm)
        }
    }

    private fun setLanguageSelected() {
        setDropdownAdapter(binding.tvLanguageDropdown, R.array.study_form_language)
        binding.tvLanguageDropdown.setOnItemClickListener { parent, _, position, _ ->
            languageSelectedItem = parent.getItemAtPosition(position).toString()
            binding.tvLanguageDropdown.setTextColor(Color.BLACK)
        }
    }

    private fun setStartDatePicker() {
        binding.tvStartPeriod.setOnClickListener {
            setDatePicker(binding.tvStartPeriod, true)
        }
    }

    private fun setEndDatePicker() {
        binding.tvEndPeriod.setOnClickListener {
            if (startDate != null) {
                setDatePicker(binding.tvEndPeriod, false)
            } else {
                R.string.study_form_validate_start_input.showSnackbar(binding.clStudyForm)
            }
        }
    }

    private fun setDatePicker(periodText: TextView, isStartDate: Boolean) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selected Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            validateDate(periodText, isStartDate, calendar.time)
        }
    }

    private fun validateDate(periodText: TextView, isStartDate: Boolean, time: Date) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val setPeriodText = dateFormat.format(time)
        val saveStartPreviousDate = startDate
        val saveEndPreviousDate = endDate
        val currentDate = Date()

        if (isStartDate) {
            startDate = time
            if (endDate == null) {
                if (dateFormat.format(currentDate) == startDate?.let { dateFormat.format(it) }) {
                    periodText.text = setPeriodText
                } else if (startDate?.before(currentDate) == true) {
                    R.string.study_form_start_before_currentDate.showSnackbar(binding.clStudyForm)
                    startDate = saveStartPreviousDate
                } else {
                    periodText.text = setPeriodText
                }
            } else {
                if (endDate!!.before(startDate)) {
                    R.string.study_form_validate_start_date.showSnackbar(binding.clStudyForm)
                    startDate = saveStartPreviousDate
                } else if (dateFormat.format(currentDate) == startDate?.let { dateFormat.format(it) }) {
                    periodText.text = setPeriodText
                } else if (startDate?.before(currentDate) == true) {
                    R.string.study_form_start_before_currentDate.showSnackbar(binding.clStudyForm)
                    startDate = saveStartPreviousDate
                } else {
                    periodText.text = setPeriodText
                }
            }
        } else {
            endDate = time
            if (endDate!!.before(startDate)) {
                R.string.study_form_validate_end_date.showSnackbar(binding.clStudyForm)
                endDate = saveEndPreviousDate
            } else if (dateFormat.format(currentDate) == endDate?.let { dateFormat.format(it) }) {
                periodText.text = setPeriodText
            } else {
                periodText.text = setPeriodText
            }
        }
    }


    private fun setDayTimeAdapter() {
        dayTimeAdapter = DayTimeAdapter(dayTimeClickListener)
        binding.rvDayTime.adapter = dayTimeAdapter
    }

    private fun setStartTimePicker(isStartTime: Boolean, dayTime: DayTime) {
        val picker = MaterialTimePicker.Builder()
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        picker.show(requireActivity().supportFragmentManager, "TimePicker")

        picker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", picker.hour, picker.minute)
            validateTime(isStartTime, dayTime, selectedTime)
        }
    }

    private fun validateTime(isStartTime: Boolean, dayTime: DayTime, selectedTime: String) {
        val position = dayTimeList.indexOf(dayTime)

        if (isStartTime) {
            val endTime = dayTime.endTime
            if ((endTime != null) && (selectedTime > endTime)) {
                R.string.study_form_validate_start_time.showSnackbar(binding.clStudyForm)
                return
            }
            dayTime.startTime = selectedTime
        } else {
            val startTime = dayTime.startTime
            if ((startTime != null) && (selectedTime < startTime)) {
                R.string.study_form_validate_end_time.showSnackbar(binding.clStudyForm)
                return
            }
            dayTime.endTime = selectedTime
        }
        if (position != -1) {
            dayTimeAdapter.notifyItemChanged(position)
        }
    }

    private fun setDaySelected() {
        val dayClickListener = View.OnClickListener { view ->
            (view as? AppCompatButton)?.let { button ->
                button.isSelected = !button.isSelected
                addScheduleForDay(button.text.toString())
            }
        }

        binding.flowDayTime.referencedIds.forEach { id ->
            val button = binding.root.findViewById<AppCompatButton>(id)
            button.setOnClickListener(dayClickListener)
        }
    }

    private fun addScheduleForDay(day: String) {
        val existingIndex = dayTimeList.indexOfFirst { it.day == day }
        if (existingIndex == -1) {
            dayTimeList.add(DayTime(day))
        } else {
            dayTimeList.removeAt(existingIndex)
        }
        dayTimeList.sortBy { getDaySort(it.day) }
        dayTimeAdapter.submitList(dayTimeList.toList())
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

    private fun setDropdownAdapter(dropdownView: AutoCompleteTextView, arrayResourceId: Int) {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            resources.getStringArray(arrayResourceId)
        )
        dropdownView.setAdapter(arrayAdapter)
    }

    private fun setTotalPeopleSelected() {
        setDropdownAdapter(binding.tvTotalPeopleDropdown, R.array.study_form_total_people)
        binding.tvTotalPeopleDropdown.setOnItemClickListener { parent, _, position, _ ->
            totalSelectedItem = parent.getItemAtPosition(position).toString()
            binding.tvTotalPeopleDropdown.setTextColor(Color.BLACK)
        }
    }

    private fun setValidateAll() {
        binding.btnCreateStudy.setOnClickListener {
            when {
                binding.ivImageInput.drawable == null -> {
                    R.string.study_form_validate_all_image.showSnackbar(binding.clStudyForm)
                }

                categorySelectedItem.isEmpty() -> {
                    R.string.study_form_validate_all_category.showSnackbar(binding.clStudyForm)
                }

                binding.etStudyNameInput.text.toString().isEmpty() -> {
                    R.string.study_form_validate_all_name.showSnackbar(binding.clStudyForm)
                }

                binding.etStudyContentInput.text.toString().isEmpty() -> {
                    R.string.study_form_validate_all_content.showSnackbar(binding.clStudyForm)
                }

                languageSelectedItem.isEmpty() -> {
                    R.string.study_form_validate_all_language.showSnackbar(binding.clStudyForm)
                }

                totalSelectedItem.isEmpty() -> {
                    R.string.study_form_validate_all_total.showSnackbar(binding.clStudyForm)
                }

                startDate == null -> {
                    R.string.study_form_validate_all_start_date.showSnackbar(binding.clStudyForm)
                }

                endDate == null -> {
                    R.string.study_form_validate_all_end_date.showSnackbar(binding.clStudyForm)
                }

                dayTimeList.isEmpty() -> {
                    R.string.study_form_validate_all_day_time_empty.showSnackbar(binding.clStudyForm)
                }

                dayTimeList.any { it.startTime == null } -> {
                    R.string.study_form_validate_all_day_time_start.showSnackbar(binding.clStudyForm)
                }

                dayTimeList.any { it.endTime == null } -> {
                    R.string.study_form_validate_all_day_time_end.showSnackbar(binding.clStudyForm)
                }

                else -> {
                    // 모든 검사가 통과되었을 때 다음 화면으로 이동하기 구현
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
