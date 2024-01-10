package com.sesac.developer_study_platform

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import com.sesac.developer_study_platform.databinding.FragmentStudyFormBinding

class StudyFormFragment : Fragment() {

    private var _binding: FragmentStudyFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                binding.sivImageInput.setImageURI(uri)
                binding.groupAddImage.visibility = View.GONE
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
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

        setCategory()
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
                Log.e("버튼 텍스트 이름", button.text.toString())
            }
        }
    }
}