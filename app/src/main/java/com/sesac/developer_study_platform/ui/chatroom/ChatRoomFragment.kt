package com.sesac.developer_study_platform.ui.chatroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.divider.MaterialDividerItemDecoration.VERTICAL
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentChatRoomBinding
import com.sesac.developer_study_platform.ui.StudyClickListener
import kotlinx.coroutines.launch

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val chatRoomAdapter = ChatRoomAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            // TODO 채팅 화면으로 이동
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

        binding.rvChatRoomList.adapter = chatRoomAdapter
        binding.rvChatRoomList.addItemDecoration(
            DividerItemDecoration(
                binding.rvChatRoomList.context,
                VERTICAL
            )
        )
        loadUserChatRoomList()
    }

    private fun loadUserChatRoomList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                service.getUserChatRoomList(Firebase.auth.uid)
            }.onSuccess {
                chatRoomAdapter.submitList(it.values.toList())
            }.onFailure {
                Log.d("ChatRoomFragment", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}