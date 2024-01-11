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
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class StudyFormFragment : Fragment() {

    private var _binding: FragmentStudyFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val dayTimeList: MutableList<DayTime> = mutableListOf<DayTime>()
    private lateinit var dayTimeAdapter: DayTimeAdapter
    private val dayTimeClickListener = object : DayTimeClickListener {
        override fun onClick(dayTime: DayTime, isStartTime: Boolean) {
            setStartTimePicker(isStartTime, dayTime)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPhotoPickMedia()
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
        setDayTimeAdapter()
        setCategory()
        setContent()
        setLanguageDropdownConnect()
        setLanguageSelected()
        setTotalDropdownConnect()
        setStartDatePicker()
        setEndDatePicker()
        setDaySelected()
        setTotalPeopleSelected()

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

    private fun setImage() {
        binding.sivImageInput.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setCategory() {
        val buttonList = binding.clContainer.children.filterIsInstance<AppCompatButton>()
        buttonList.forEach { button ->
            button.setOnClickListener {
                buttonList.forEach { it.isSelected = false }
                button.isSelected = true
                Log.e("Selected Button", button.text.toString())
            }
        }
    }

    private fun setContent() {
        binding.etStudyContentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val lineCount = binding.etStudyContentInput.lineCount
                val inputText = binding.etStudyContentInput.layout
                if (lineCount > 4) {
                    for (i in 0 until lineCount - 4) {
                        val lastLineStartText = inputText.getLineStart(lineCount - 1)
                        val lastLineEndText = inputText.getLineEnd(lineCount - 1)
                        binding.etStudyContentInput.text.delete(lastLineStartText, lastLineEndText)
                    }
                }
            }
        })
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
            val selectedItem = parent.getItemAtPosition(position).toString()
            Log.e("selected Language", selectedItem)
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

    private fun setDatePicker(periodText: TextView) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selected Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(childFragmentManager, "timePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            periodText.text = dateFormat.format(calendar.time)
        }
    }

    private fun setStartDatePicker() {
        binding.tvStartPeriod.setOnClickListener {
            setDatePicker(binding.tvStartPeriod)
        }
    }

    private fun setEndDatePicker() {
        binding.tvEndPeriod.setOnClickListener {
            setDatePicker(binding.tvEndPeriod)
        }
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
            if (isStartTime) {
                dayTime.startTime = selectedTime
            } else {
                dayTime.endTime = selectedTime
            }

            val position = dayTimeList.indexOf(dayTime)
            if (position != -1) {
                dayTimeAdapter.notifyItemChanged(position)
            }
        }
    }

    private fun setDayTimeAdapter() {
        dayTimeAdapter = DayTimeAdapter(dayTimeList, dayTimeClickListener)
        binding.rvDayTime.adapter = dayTimeAdapter
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

    private fun setTotalPeopleSelected() {
        binding.actvTotalPeopleDropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Log.e("selected TotalPeople", selectedItem)
        }
    }
}
