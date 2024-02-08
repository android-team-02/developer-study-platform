package com.sesac.developer_study_platform.ui.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.divider.MaterialDividerItemDecoration.VERTICAL
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.databinding.FragmentChatRoomBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ChatRoomViewModel>()
    private val chatRoomAdapter = ChatRoomAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            viewModel.moveToMessage(sid)
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

        setChatRoomAdapter()
        loadChatRoomList()
        setNavigation()
    }

    private fun setChatRoomAdapter() {
        binding.rvChatRoomList.adapter = chatRoomAdapter
        binding.rvChatRoomList.addItemDecoration(
            DividerItemDecoration(binding.rvChatRoomList.context, VERTICAL)
        )
    }

    private fun loadChatRoomList() {
        viewModel.loadStudyList()
        viewModel.chatRoomListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                chatRoomAdapter.submitList(it)
            }
        )
    }

    private fun setNavigation() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = ChatRoomFragmentDirections.actionGlobalToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}