package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.databinding.FragmentMessageBinding
import java.time.LocalDateTime
import java.util.Date

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MessageFragmentArgs>()
    private val messageAdapter = MessageAdapter()
    private val viewModel by viewModels<MessageViewModel>()
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_ITEM_COUNT)) {
            saveMultipleMedia(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStudyName()
        binding.rvMessageList.adapter = messageAdapter
        loadMessageList()
        binding.ivPlus.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.ivSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadStudyName() {
        viewModel.loadStudyName(args.studyId)
        viewModel.studyNameEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.toolbar.title = it
            }
        )
    }

    private fun loadMessageList() {
        viewModel.loadMessageList(args.studyId)
        viewModel.messageListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                messageAdapter.submitList(it.values.toList())
            }
        )
    }

    private fun saveMultipleMedia(uriList: List<Uri>) {
        val timestamp = getTimestamp()
        viewModel.saveMultipleMedia(args.studyId, uriList, timestamp)
        viewModel.addUriListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                sendImage(it, timestamp)
            }
        )
    }

    private fun sendImage(uriList: List<Uri>, timestamp: String) {
        viewModel.sendImage(args.studyId, uriList, timestamp)
        viewModel.addMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                loadMessageList()
            }
        )
    }

    private fun sendMessage() {
        viewModel.sendMessage(args.studyId, binding.etMessageInput.text.toString())
        viewModel.addMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.etMessageInput.text.clear()
                loadMessageList()
            }
        )
    }

    private fun getTimestamp(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            Date().toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAX_ITEM_COUNT = 9
    }
}