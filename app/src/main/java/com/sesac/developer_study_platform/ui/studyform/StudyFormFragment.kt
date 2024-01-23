package com.sesac.developer_study_platform.ui.studyform

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import com.sesac.developer_study_platform.ui.DayTimeClickListener
import com.sesac.developer_study_platform.util.showSnackbar
import kotlinx.coroutines.launch
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
    private lateinit var selectedImageUri: Uri
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

    private fun setSelectedImage(imageUri: Uri?) {
        if (imageUri != null) {
            binding.ivImageInput.setImageURI(imageUri)
            binding.groupAddImage.visibility = View.GONE
            selectedImageUri = imageUri
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

        val nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateName(name)
            }
        }

        val contentTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateContent(content)
            }
        }

        name.addTextChangedListener(nameTextWatcher)
        content.addTextChangedListener(contentTextWatcher)
    }

    private fun validateName(name: EditText) {
        if (name.text.toString().length == 20) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_name)
        }
    }

    private fun validateContent(content: EditText) {
        if (content.text.toString().length == 150) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_content)
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
        binding.tvStartDate.setOnClickListener {
            setDatePicker(binding.tvStartDate, true)
        }
    }

    private fun setEndDatePicker() {
        binding.tvEndDate.setOnClickListener {
            if (startDate != null) {
                setDatePicker(binding.tvEndDate, false)
            } else {
                binding.clStudyForm.showSnackbar(R.string.study_form_validate_start_date_input)
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
                validateStartDateBeforeCurrentDate(
                    dateFormat,
                    currentDate,
                    periodText,
                    setPeriodText,
                    saveStartPreviousDate
                )
            } else {
                validateStartDate(
                    saveStartPreviousDate,
                    dateFormat,
                    currentDate,
                    periodText,
                    setPeriodText
                )
            }
        } else {
            validateEndDate(
                time,
                saveEndPreviousDate,
                dateFormat,
                currentDate,
                periodText,
                setPeriodText
            )
        }
    }

    private fun validateStartDateBeforeCurrentDate(
        dateFormat: SimpleDateFormat,
        currentDate: Date,
        periodText: TextView,
        setPeriodText: String?,
        saveStartPreviousDate: Date?
    ) {
        if (dateFormat.format(currentDate) == startDate?.let { dateFormat.format(it) }) {
            periodText.text = setPeriodText
        } else if (startDate?.before(currentDate) == true) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_start_date_before_today)
            startDate = saveStartPreviousDate
        } else {
            periodText.text = setPeriodText
        }
    }

    private fun validateStartDate(
        saveStartPreviousDate: Date?,
        dateFormat: SimpleDateFormat,
        currentDate: Date,
        periodText: TextView,
        setPeriodText: String?
    ) {
        if (endDate!!.before(startDate)) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_start_date)
            startDate = saveStartPreviousDate
        } else if (dateFormat.format(currentDate) == startDate?.let { dateFormat.format(it) }) {
            periodText.text = setPeriodText
        } else if (startDate?.before(currentDate) == true) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_start_date_before_today)
            startDate = saveStartPreviousDate
        } else {
            periodText.text = setPeriodText
        }
    }

    private fun validateEndDate(
        time: Date,
        saveEndPreviousDate: Date?,
        dateFormat: SimpleDateFormat,
        currentDate: Date,
        periodText: TextView,
        setPeriodText: String?
    ) {
        endDate = time
        if (endDate!!.before(startDate)) {
            binding.clStudyForm.showSnackbar(R.string.study_form_validate_end_date)
            endDate = saveEndPreviousDate
        } else if (dateFormat.format(currentDate) == endDate?.let { dateFormat.format(it) }) {
            periodText.text = setPeriodText
        } else {
            periodText.text = setPeriodText
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
                binding.clStudyForm.showSnackbar(R.string.study_form_validate_start_time)
                return
            }
            dayTime.startTime = selectedTime
        } else {
            val startTime = dayTime.startTime
            if ((startTime != null) && (selectedTime < startTime)) {
                binding.clStudyForm.showSnackbar(R.string.study_form_validate_end_time)
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
        setDropdownAdapter(binding.tvPeopleDropdown, R.array.study_form_people)
        binding.tvPeopleDropdown.setOnItemClickListener { parent, _, position, _ ->
            totalSelectedItem = parent.getItemAtPosition(position).toString()
            binding.tvPeopleDropdown.setTextColor(Color.BLACK)
        }
    }

    private fun setValidateAll() {
        binding.btnCreateStudy.setOnClickListener {
            when {
                binding.ivImageInput.drawable == null -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_image)
                }

                categorySelectedItem.isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_category)
                }

                binding.etStudyNameInput.text.toString().isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_name_input)
                }

                binding.etStudyContentInput.text.toString().isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_content_input)
                }

                languageSelectedItem.isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_language)
                }

                startDate == null -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_start_date)
                }

                endDate == null -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_end_date)
                }

                dayTimeList.isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_day)
                }

                dayTimeList.any { it.startTime == null } -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_start_time)
                }

                dayTimeList.any { it.endTime == null } -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_end_time)
                }

                totalSelectedItem.isEmpty() -> {
                    binding.clStudyForm.showSnackbar(R.string.study_form_validate_select_people)
                }

                else -> {
                    val uid = Firebase.auth.uid
                    if (uid != null) {
                        val sid = formatSid(uid)
                        uploadImageStorage(sid, selectedImageUri) { fileName ->
                            putFirebase(sid, uid, fileName)
                        }
                    }
                }
            }
        }
    }

    private fun uploadImageStorage(sid: String, imageUri: Uri, onUploadSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val fileName = "image_${binding.etStudyNameInput.text}.jpg"
        val imageRef = storageRef.child("$sid/$fileName")

        imageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                onUploadSuccess(fileName)
            }?.addOnFailureListener {
                Log.e("download URL", "download URL Failed", it)
            }
        }.addOnFailureListener { exception ->
            Log.e("UploadImage", "Upload failed", exception)
        }
    }

    private fun putFirebase(sid: String, uid: String, fileName: String) {
        val newStudy = getStudy(sid, uid, fileName)
        val userStudyRoom = getUserStudy(sid, fileName)
        tryPutStudy(sid, newStudy)
        tryPutUserStudyRoom(uid, sid, userStudyRoom)
    }

    private fun formatSid(uid: String): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        return "@make@$uid@time@$timestamp"
    }

    private fun getStudy(sid: String, uid: String, fileName: String): Study {
        return Study(
            sid = sid,
            name = binding.etStudyNameInput.text.toString(),
            image = fileName,
            content = binding.etStudyContentInput.text.toString(),
            category = categorySelectedItem,
            language = languageSelectedItem,
            totalMemberCount = totalSelectedItem.toInt(),
            days = formatDays(),
            startDate = binding.tvStartDate.text.toString(),
            endDate = binding.tvEndDate.text.toString(),
            members = mapOf(uid to true),
            banUsers = mapOf("default" to true)
        )
    }

    private fun getUserStudy(sid: String, fileName: String): UserStudy {
        return UserStudy(
            sid = sid,
            name = binding.etStudyNameInput.text.toString(),
            image = fileName,
            language = languageSelectedItem,
            days = formatDays()
        )
    }

    private fun formatDays() = dayTimeList.associate {
        it.day to "${it.startTime?.replace(":", "")}@${it.endTime?.replace(":", "")}"
    }

    private fun tryPutStudy(sid: String, newStudy: Study) {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.putStudy(sid, newStudy)
            }.onSuccess {
                Log.d("StudyFormFragment-Study", "success")
            }.onFailure {
                Log.e("StudyFormFragment-Study", it.message ?: "error occurred.")
            }
        }
    }

    private fun tryPutUserStudyRoom(uid: String, sid: String, userStudyRoom: UserStudy) {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.putUserStudyRoom(uid, sid, userStudyRoom)
            }.onSuccess {
                Log.d("StudyFormFragment-UserStudyRoom", "success")
            }.onFailure {
                Log.e("StudyFormFragment-UserStudyRoom", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
