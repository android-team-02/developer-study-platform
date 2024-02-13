package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.StudyMember
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMessageBinding
import com.sesac.developer_study_platform.util.getTimestamp
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false)
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
        setExitButton()
        setExitButtonVisibility()
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

    private fun loadMenuMemberList() {
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getStudyMemberList(args.studyId)
            }.onSuccess { member ->
                loadUsers(member)
            }.onFailure {
                Log.e("MessageFragment-loadMenuMemberList", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun loadUsers(member: Map<String, Boolean>) {
        val memberList = mutableListOf<StudyMember>()
        lifecycleScope.async {
            member.forEach { (uid, isAdmin) ->
                kotlin.runCatching {
                    service.getUserById(uid)
                }.onSuccess { studyUser ->
                    memberList.add(StudyMember(studyUser, isAdmin, uid))
                }.onFailure {
                    Log.e("MessageFragment-loadUsers", it.message ?: "error occurred.")
                }.getOrNull()
            }
        }.await()
        val sortMembers = memberList.sortedByDescending { it.isAdmin }
        menuAdapter.submitList(sortMembers)
    }

    private fun setExitButton() {
        binding.ivExit.setOnClickListener {
            viewModel.moveToExitDialog(args.studyId)
        }
    }

    private fun setExitButtonVisibility() {
        viewModel.checkAdmin(args.studyId)
        viewModel.isAdminEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.isAdmin = it
            }
        )
    }

    private fun setNavigation() {
        moveToBack()
        moveToExitDialog()
    }

    private fun moveToBack() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
    }

    private fun moveToExitDialog() {
        viewModel.moveToExitDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = MessageFragmentDirections.actionMessageToExitDialog(it)
                findNavController().navigate(action)
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