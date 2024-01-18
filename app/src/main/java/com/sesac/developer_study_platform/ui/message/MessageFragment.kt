package com.sesac.developer_study_platform.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
        if (messageAdapter.currentList.isNotEmpty()) {
            loadMessageList()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}