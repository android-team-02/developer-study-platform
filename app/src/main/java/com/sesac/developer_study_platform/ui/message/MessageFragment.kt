package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMessageBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val chatRoomId = "@make@abcd@time@20240111144250"
    private val messageAdapter = MessageAdapter()
    private val uid = Firebase.auth.uid
    private val storageRef = Firebase.storage.reference
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(9)) { uriList ->
            saveMultipleMedia(uriList)
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

        binding.rvMessageList.adapter = messageAdapter
        loadMessageList()
        binding.ivPlus.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.ivSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun saveMultipleMedia(uriList: List<Uri>) {
        if (uriList.isNotEmpty()) {
            uriList.forEach { uri ->
                val imagesRef = storageRef.child("$chatRoomId/image_${uri.lastPathSegment}.jpg")
                val uploadTask = imagesRef.putFile(uri)
                uploadTask.addOnFailureListener {
                    Log.e("MessageFragment-selectMultipleMedia", it.message ?: "error occurred.")
                }
            }
        }
    }

    private fun loadMessageList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getMessageList(chatRoomId)
            }.onSuccess {
                messageAdapter.submitList(it.values.toList())
                it.keys.forEach { messageId ->
                    updateReadUserList(messageId)
                }
                updateUnreadUserCount()
            }.onFailure {
                Log.e("MessageFragment-loadMessageList", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateReadUserList(messageId: String) {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.updateReadUserList(chatRoomId, messageId, getReadUserList(messageId))
            }.onFailure {
                Log.e("MessageFragment-updateReadUserList", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun getReadUserList(messageId: String): Map<String, Boolean> {
        val service = StudyService.create()
        return lifecycleScope.async {
            kotlin.runCatching {
                service.getReadUserList(chatRoomId, messageId)
            }.onSuccess {
                if (uid != null) {
                    it[uid] = true
                }
            }.onFailure {
                Log.e("MessageFragment-getReadUserList", it.message ?: "error occurred.")
            }.getOrDefault(mapOf())
        }.await()
    }

    private fun updateUnreadUserCount() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.updateUnreadUserCount(chatRoomId, uid)
            }.onFailure {
                Log.e("MessageFragment-updateUnreadUserCount", it.message ?: "error occurred.")
            }
        }
    }

    private fun sendMessage() {
        val service = StudyService.create()
        lifecycleScope.launch {
            val message = Message(
                uid,
                getUser(),
                isAdmin(),
                binding.etMessageInput.text.toString(),
                getStudyMemberCount(),
                mapOf(uid to true)
            )
            kotlin.runCatching {
                service.addMessage(chatRoomId, message)
            }.onSuccess {
                loadMessageList()
                getStudyMemberList()
                updateLastMessage(message)
                binding.etMessageInput.text.clear()
            }.onFailure {
                Log.e("MessageFragment-sendMessage", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun getUser(): StudyUser? {
        val service = StudyService.create()
        return lifecycleScope.async {
            kotlin.runCatching {
                uid?.let { service.getUserById(it) }
            }.onFailure {
                Log.e("MessageFragment-getUser", it.message ?: "error occurred.")
            }.getOrNull()
        }.await()
    }

    private suspend fun isAdmin(): Boolean {
        val service = StudyService.create()
        return lifecycleScope.async {
            kotlin.runCatching {
                service.isAdmin(chatRoomId, uid)
            }.onFailure {
                Log.e("MessageFragment-isAdmin", it.message ?: "error occurred.")
            }.getOrDefault(false)
        }.await()
    }

    private suspend fun getStudyMemberCount(): Int {
        val service = StudyService.create()
        return lifecycleScope.async {
            kotlin.runCatching {
                service.getStudyMemberList(chatRoomId)
            }.mapCatching {
                it.count()
            }.onFailure {
                Log.e("MessageFragment-getStudyMemberCount", it.message ?: "error occurred.")
            }.getOrDefault(0)
        }.await()
    }

    private fun getStudyMemberList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getStudyMemberList(chatRoomId)
            }.onSuccess {
                it.keys.forEach { member ->
                    if (messageAdapter.currentList.isNotEmpty() && member != uid) {
                        getUnreadUserList(member)
                    }
                }
            }.onFailure {
                Log.e("MessageFragment-getStudyMemberList", it.message ?: "error occurred.")
            }
        }
    }

    private fun getUnreadUserList(member: String) {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getUnreadUserList(chatRoomId)
            }.onSuccess {
                updateUnreadUserCount(member, it)
            }.onFailure {
                Log.e("MessageFragment-getUnreadUserList", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateUnreadUserCount(member: String, unreadUserList: Map<String, Int>) {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.updateUnreadUserCount(
                    chatRoomId,
                    member,
                    unreadUserList.getOrDefault(member, 0) + 1
                )
            }.onFailure {
                Log.e("MessageFragment-updateUnreadUserCount", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateLastMessage(message: Message) {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.updateLastMessage(chatRoomId, message)
            }.onFailure {
                Log.e("MessageFragment-updateLastMessage", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}