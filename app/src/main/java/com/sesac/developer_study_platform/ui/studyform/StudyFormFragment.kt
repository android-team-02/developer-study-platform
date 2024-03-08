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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import com.sesac.developer_study_platform.util.isNetworkConnected
import com.sesac.developer_study_platform.util.DateFormats
import com.sesac.developer_study_platform.util.formatTimestamp
import com.sesac.developer_study_platform.util.setImage
import com.sesac.developer_study_platform.util.showSnackbar
import kotlinx.coroutines.launch
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
    private val studyService = StudyService.create()
    private val viewModel by viewModels<StudyFormViewModel>()
    private val dayTimeAdapter = DayTimeAdapter(object : DayTimeClickListener {
        override fun onClick(isStartTime: Boolean, dayTime: DayTime) {
            showTimePicker(isStartTime, dayTime)
        }
    })
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setImageUri(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_study_form, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setImageButton()
        setImageVisibility()
        setSelectedImage()
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
        setPositiveButton()
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
        validateTime()
        setTotalPeopleCount()
        setValidateAll()
        binding.isNetworkConnected = isNetworkConnected(requireContext())
    }

    private fun setImageButton() {
        binding.ivImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setImageVisibility() {
        viewModel.isSelectedImage.observe(viewLifecycleOwner, EventObserver {
            binding.isSelectedImage = it
        })
    }

    private fun setSelectedImage() {
        viewModel.imageUriEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.ivImage.setImageURI(it)
            })
    }

    private fun setCategoryButton(button: AppCompatButton) {
        val buttonList = binding.flowCategory.referencedIds.map { id ->
            binding.root.findViewById<AppCompatButton>(id)
        }
        button.setOnClickListener {
            viewModel.selectCategory(button.text.toString())
            button.isSelected = true
            buttonList.forEach {
                if (it != button) {
                    it.isSelected = false
                }
            }
        }
    }

    private fun validateName() {
        binding.etStudyNameInput.addTextChangedListener(
            CustomTextWatcher {
                viewModel.validateName(it)
            }
        )
        viewModel.isNameValidate.observe(
            viewLifecycleOwner,
            EventObserver {
                if (it) {
                    binding.root.showSnackbar(R.string.study_form_validate_name)
                }
            })
    }

    private fun validateContent() {
        binding.etStudyContentInput.addTextChangedListener(
            CustomTextWatcher {
                viewModel.validateContent(it)
            }
        )
        viewModel.isContentValidate.observe(
            viewLifecycleOwner,
            EventObserver {
                if (it) {
                    binding.root.showSnackbar(R.string.study_form_validate_content)
                }
            })
    }

    private fun setLanguage() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            resources.getStringArray(R.array.study_form_language)
        )
        binding.tvLanguageDropdown.setAdapter(arrayAdapter)
        binding.tvLanguageDropdown.setOnItemClickListener { parent, _, position, _ ->
            viewModel.selectLanguage(parent.getItemAtPosition(position).toString())
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
            viewModel.selectTotalCount(parent.getItemAtPosition(position).toString().toInt())
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
        selectedDateRange(dateRangePicker)
    }

    private fun selectedDateRange(dateRangePicker: MaterialDatePicker<Pair<Long, Long>>) {
        dateRangePicker.addOnPositiveButtonClickListener {
            viewModel.selectDateRange(it.first, it.second)
        }
    }

    private fun setPositiveButton() {
        viewModel.startDateEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.tvStartDate.text = it
            })

        viewModel.endDateEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.tvEndDate.text = it
            })
    }

    private fun showTimePicker(isStartTime: Boolean, dayTime: DayTime) {
        val timePicker = MaterialTimePicker.Builder()
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        timePicker.show(requireActivity().supportFragmentManager, "TimePicker")

        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            viewModel.validateTime(isStartTime, dayTime, selectedTime)
        }
    }

    private fun validateTime() {
        viewModel.dayTimeErrorMessageEvent.observe(viewLifecycleOwner, EventObserver { messageId ->
            binding.root.showSnackbar(messageId)
        })

        viewModel.dayTimeListEvent.observe(viewLifecycleOwner, EventObserver { dayTimeList ->
            dayTimeAdapter.submitList(dayTimeList)
            dayTimeList.forEach { updatedDayTime ->
                val index = dayTimeAdapter.currentList.indexOfFirst { it.day == updatedDayTime.day }
                if (index != -1) {
                    dayTimeAdapter.notifyItemChanged(index)
                }
                Log.d("DayTimeList", "Day: ${updatedDayTime.day}, StartTime: ${updatedDayTime.startTime}, EndTime: ${updatedDayTime.endTime}")
            }
        })
    }

    private fun setDayButton(button: AppCompatButton) {
        button.setOnClickListener {
            button.isSelected = !button.isSelected
            viewModel.addScheduleForDay(button.text.toString())
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
                            saveChatRoom(sid)
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(sid: String, image: Uri, onUploadSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val fileName = "image_${binding.etStudyNameInput.text}.jpg"
        val imageRef = storageRef.child("$sid/$fileName")

        imageRef.putFile(image).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                onUploadSuccess(fileName)
            }?.addOnFailureListener {
                Log.e("StudyFormFragment-uploadImage", it.message ?: "error occurred.")
            }
        }.addOnFailureListener {
            Log.e("StudyFormFragment-uploadImage", it.message ?: "error occurred.")
        }
    }

    private fun saveStudy(sid: String, study: Study) {
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.putStudy(sid, study)
            }.onFailure {
                Log.e("StudyFormFragment-saveStudy", it.message ?: "error occurred.")
            }
        }
    }

    private fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.putUserStudy(uid, sid, userStudy)
            }.onFailure {
                Log.e("StudyFormFragment-saveUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    private fun saveChatRoom(sid: String) {
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.addChatRoom(sid, ChatRoom())
            }.onSuccess {
                val action = StudyFormFragmentDirections.actionStudyFormToMessage(sid)
                findNavController().navigate(action)
            }.onFailure {
                Log.e("StudyFormFragment-saveChatRoom", it.message ?: "error occurred.")
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
