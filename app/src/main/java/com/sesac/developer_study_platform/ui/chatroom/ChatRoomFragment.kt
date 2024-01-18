package com.sesac.developer_study_platform.ui.chatroom

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
import com.sesac.developer_study_platform.databinding.FragmentChatRoomBinding
import com.sesac.developer_study_platform.ui.ChatRoomClickListener
import kotlinx.coroutines.launch

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val chatRoomAdapter = ChatRoomAdapter(object : ChatRoomClickListener {
        override fun onClick(sid: String) {
            // 채팅 화면으로 이동
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvChatRoom.adapter = chatRoomAdapter
        loadUserChatRoomList()
    }

    private fun loadUserChatRoomList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getUserChatRoomList(Firebase.auth.uid)
            }.onSuccess {
                chatRoomAdapter.submitList(it.values.toList())
                Log.d("ChatRoomFragment", it.toString())
            }.onFailure {
                Log.d("ChatRoomFragment", it.message ?: "error occurred.")
            }
        }
    }
}