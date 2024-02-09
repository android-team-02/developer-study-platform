package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.data.StudyMember
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMessageBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MessageFragmentArgs>()
    private val messageAdapter = MessageAdapter()
    private val viewModel by viewModels<MessageViewModel>()
    private val service = StudyService.create()
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_ITEM_COUNT)) {
            saveMultipleMedia(it)
        }
    private val menuAdapter = MenuAdapter(object : StudyMemberClickListener {
        override fun onClick(uid: String) {
            val action = MessageFragmentDirections.actionMessageToProfile(uid)
            findNavController().navigate(action)
        }
    })

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

        binding.rvMessageList.adapter = messageAdapter
        binding.rvMemberList.adapter = menuAdapter
        setBackButton()
        setMenuButton()
        loadStudyName()
        loadMessageList()
        loadMenuMemberList()
        setPlusButton()
        setSendButton()
        setNavigation()
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun setMenuButton() {
        binding.ivMenu.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.END)
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

    private fun loadMenuMemberList() {
        viewModel.loadMemberList(args.studyId)
        viewModel.studyMemberListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                loadUsers(it)
            }
        )
    }

    private fun loadUsers(members: Map<String, Boolean>) {
        lifecycleScope.launch {
            viewModel.loadUsers(members)
        }
        viewModel.userEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                menuAdapter.submitList(it)
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

    private fun setPlusButton() {
        binding.ivPlus.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setSendButton() {
        binding.ivSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setNavigation() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAX_ITEM_COUNT = 9
    }
}