package com.sesac.developer_study_platform

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var dayTimeAdapter: DayTimeAdapter
    private val dayTimeList: MutableList<DayTime> = mutableListOf()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImage()
        setPhotoPickMedia()
        setCategory()
        setValidateText()
        setLanguageDropdownConnect()
        setLanguageSelected()
        setStartDatePicker()
        setEndDatePicker()
        setDayTimeAdapter()
        setDaySelected()
        setTotalDropdownConnect()
        setTotalPeopleSelected()
        setValidateAll()

    }

    private fun setImage() {
        binding.sivImageInput.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setPhotoPickMedia() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                binding.sivImageInput.setImageURI(uri)
                binding.groupAddImage.visibility = View.GONE
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun setCategory() {
        val buttonList = binding.clContainer.children.filterIsInstance<AppCompatButton>()
        buttonList.forEach { button ->
            button.setOnClickListener {
                buttonList.forEach { it.isSelected = false }
                button.isSelected = true
                categorySelectedItem = button.isSelected.toString()
            }
        }
    }

    private fun setValidateText() {
        val name = binding.etStudyNameInput
        val content = binding.etStudyContentInput

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateContent(content)
                validateName(name)

            }
        }
        name.addTextChangedListener(textWatcher)
        content.addTextChangedListener(textWatcher)
    }

    private fun validateContent(content: EditText) {
        if (content.lineCount > 4) {
            val lastLineIndex = content.lineCount - 1
            val lastLineStartText = content.layout.getLineStart(lastLineIndex)
            val lastLineEndText = content.layout.getLineEnd(lastLineIndex)
            content.text.delete(lastLineStartText, lastLineEndText)
            showSnackbar(R.string.study_form_validate_content)
        }
    }

    private fun validateName(name: EditText) {
        if (name.text.toString().length > 20) {
            val newText = name.text.toString().substring(0, 20)
            name.setText(newText)
            name.setSelection(newText.length)
            showSnackbar(R.string.study_form_validate_name)
        }
    }

    private fun setLanguageDropdownConnect() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.itme_dropdown,
            resources.getStringArray(R.array.study_form_language)
        )
        binding.actvLanguageDropdown.setAdapter(arrayAdapter)
    }

    private fun setLanguageSelected() {
        binding.actvLanguageDropdown.setOnItemClickListener { parent, _, position, _ ->
            languageSelectedItem = parent.getItemAtPosition(position).toString()
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
                showSnackbar(R.string.study_form_validate_start_input)
            }
        }
    }

    private fun setDatePicker(periodText: TextView, isStartDate: Boolean) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selected Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(childFragmentManager, "timePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it

            validateDate(periodText, isStartDate, calendar.time)
        }
    }

    private fun validateDate(periodText: TextView, isStartDate: Boolean, time: Date) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val setPeriodText = dateFormat.format(time)

        if (isStartDate) {
            val saveStartPreviousDate = startDate
            startDate = time
            if (endDate == null) {
                periodText.text = setPeriodText
            } else {
                if (endDate!!.before(startDate)) {
                    showSnackbar(R.string.study_form_validate_start_date)
                    startDate = saveStartPreviousDate
                } else {
                    periodText.text = setPeriodText
                }
            }
        } else {
            val saveEndPreviousDate = endDate
            endDate = time
            if (endDate!!.before(startDate)) {
                showSnackbar(R.string.study_form_validate_end_date)
                endDate = saveEndPreviousDate
            } else {
                periodText.text = setPeriodText
            }
        }
    }

    private fun setDayTimeAdapter() {
        dayTimeAdapter = DayTimeAdapter(dayTimeList, dayTimeClickListener)
        binding.rvDayTime.adapter = dayTimeAdapter
    }

    private fun setStartTimePicker(isStartTime: Boolean, dayTime: DayTime) {
        val picker = MaterialTimePicker.Builder()
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .build()
        picker.show(requireActivity().supportFragmentManager, "TimePicker")

        picker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", picker.hour, picker.minute)
            validateTime(isStartTime, dayTime, selectedTime, picker.hour)
        }
    }

    private fun validateTime(isStartTime: Boolean, dayTime: DayTime, selectedTime: String, hour: Int) {
        val isAM = hour < 12
        if (isStartTime) {
            if (isAM) {
                dayTime.startTime = selectedTime
            } else {
                showSnackbar(R.string.study_form_validate_am)
            }
        } else {
            if (isAM) {
                showSnackbar(R.string.study_form_validate_pm)
            } else {
                dayTime.endTime = selectedTime
            }
        }

        val position = dayTimeList.indexOf(dayTime)
        if (position != -1) {
            dayTimeAdapter.notifyItemChanged(position)
        }
        Log.e("데이터피커", dayTimeList.toString())
    }

    private fun setDaySelected() {
        binding.flowDayTime.referencedIds.forEach { id ->
            val button = binding.root.findViewById<AppCompatButton>(id)
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                addScheduleForDay(button.text.toString())
            }
        }
    }

    private fun addScheduleForDay(day: String) {
        val existingIndex = dayTimeList.indexOfFirst { it.day == day }
        if (existingIndex == -1) {
            val newSchedule = DayTime(day)
            dayTimeList.add(newSchedule)
            dayTimeAdapter.notifyItemInserted(dayTimeList.size - 1)
        } else {
            dayTimeList.removeAt(existingIndex)
            dayTimeAdapter.notifyItemRemoved(existingIndex)
        }
    }

    private fun setTotalDropdownConnect() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.itme_dropdown,
            resources.getStringArray(R.array.study_form_total_people)
        )
        binding.actvTotalPeopleDropdown.setAdapter(arrayAdapter)
    }

    private fun setTotalPeopleSelected() {
        binding.actvTotalPeopleDropdown.setOnItemClickListener { parent, view, position, id ->
            totalSelectedItem = parent.getItemAtPosition(position).toString()
        }
    }

    private fun setValidateAll() {
        binding.tvCreateStudy.setOnClickListener {
            if (binding.etStudyNameInput.text.toString().isEmpty() ||
                binding.etStudyContentInput.text.toString().isEmpty() ||
                languageSelectedItem.isEmpty() || totalSelectedItem.isEmpty() ||
                categorySelectedItem.isEmpty() || dayTimeList.isEmpty() ||
                dayTimeList.any { it.startTime == null || it.endTime == null } ||
                startDate == null || endDate == null || binding.sivImageInput.drawable == null
            ) {
                showSnackbar(R.string.study_form_validate_all)
            } else {
                //다음 화면으로 이동하기 구현
            }
        }
    }

    private fun showSnackbar(resId: Int) {
        Snackbar.make(
            binding.clStudyForm,
            resId,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
