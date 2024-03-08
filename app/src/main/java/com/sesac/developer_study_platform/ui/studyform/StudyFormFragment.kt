package com.sesac.developer_study_platform.ui.studyform

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
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding
import com.sesac.developer_study_platform.util.formatTimestamp
import com.sesac.developer_study_platform.util.isNetworkConnected
import com.sesac.developer_study_platform.util.showSnackbar
import kotlinx.coroutines.launch

class StudyFormFragment : Fragment() {

    private var _binding: FragmentStudyFormBinding? = null
    private val binding get() = _binding!!
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
            val dayTimeList = viewModel.dayTimeListEvent.value?.peekContent() ?: emptyList()
            when {
                viewModel.isSelectedImage.value?.peekContent() == false -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_image)
                }

                viewModel.selectedCategory.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_category)
                }

                viewModel.nameEvent.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_name_input)
                }

                viewModel.contentEvent.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_content_input)
                }

                viewModel.selectedLanguage.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_language)
                }

                viewModel.startDateEvent.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_start_date)
                }

                viewModel.endDateEvent.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_end_date)
                }

                dayTimeList.isEmpty() -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_day)
                }

                dayTimeList.any { it.startTime.isNullOrEmpty() } -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_start_time)
                }

                dayTimeList.any { it.endTime.isNullOrEmpty() } -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_end_time)
                }

                viewModel.selectedTotalCountEvent.value == null -> {
                    binding.root.showSnackbar(R.string.study_form_validate_select_people)
                }

                else -> {
                    val uid = Firebase.auth.uid
                    val sid = "@make@$uid@time@${formatTimestamp()}"
                    uploadImage(sid)

                    viewModel.uploadImageEvent.observe(
                        viewLifecycleOwner,
                        EventObserver { fileName ->
                            uid?.let {
                                saveStudy(sid, formatStudy(sid, uid, fileName))
                                saveUserStudy(uid, sid, formatUserStudy(sid, fileName))
                                saveChatRoom(sid)
                            }
                        })
                }
            }
        }
    }

    private fun uploadImage(sid: String) {
        val fileName = "image_${binding.etStudyNameInput.text}.jpg"
        viewModel.uploadImage(sid, fileName)
    }

    private fun saveStudy(sid: String, study: Study) {
        lifecycleScope.launch {
            viewModel.saveStudy(sid, study)
        }
    }

    private fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        lifecycleScope.launch {
            viewModel.saveUserStudy(uid, sid, userStudy)
        }
    }

    private fun saveChatRoom(sid: String) {
        lifecycleScope.launch {
            viewModel.saveChatRoom(sid)
        }

        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = StudyFormFragmentDirections.actionStudyFormToMessage(sid)
                findNavController().navigate(action)
            }
        )
    }

    private fun formatStudy(sid: String, uid: String, fileName: String): Study {
        return viewModel.formatStudy(sid, uid, fileName)
    }

    private fun formatUserStudy(sid: String, fileName: String): UserStudy {
        return viewModel.formatUserStudy(sid, fileName)
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
