package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentMessageBinding
import com.sesac.developer_study_platform.util.isNetworkConnected

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MessageFragmentArgs>()
    private val viewModel by viewModels<MessageViewModel>()
    private var isBottom = true
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_ITEM_COUNT)) {
            saveMultipleMedia(it)
        }
    private val messageAdapter = MessageAdapter(object : ImageClickListener {
        override fun onClick(imageUrl: String) {
            val action = MessageFragmentDirections.actionMessageToImage(imageUrl)
            findNavController().navigate(action)
        }
    })
    private val menuAdapter = MenuAdapter(object : StudyMemberClickListener {
        override fun onClick(sid: String, uid: String) {
            val action = MessageFragmentDirections.actionMessageToProfile(sid, uid)
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
        setMessageScrolled()
        loadMenuMemberList()
        setPlusButton()
        setSendButton()
        setExitButton()
        setExitButtonVisibility()
        setNavigation()
        binding.isNetworkConnected = isNetworkConnected(requireContext())
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
                messageAdapter.submitList(it.values.toList()) {
                    if (isBottom) {
                        binding.rvMessageList.scrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        )
    }

    private fun setMessageScrolled() {
        binding.rvMessageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                isBottom = layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1
            }
        })
    }

    private fun saveMultipleMedia(uriList: List<Uri>) {
        val timestamp = System.currentTimeMillis()
        viewModel.saveMultipleMedia(args.studyId, uriList, timestamp)
        viewModel.addUriListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                sendImage(it, timestamp)
            }
        )
    }

    private fun sendImage(uriList: List<Uri>, timestamp: Long) {
        viewModel.sendImage(args.studyId, uriList, timestamp)
        viewModel.addMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                viewModel.loadStudyMemberList(args.studyId)
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
                viewModel.loadStudyMemberList(args.studyId)
                loadMessageList()
            }
        )
    }

    private fun loadMenuMemberList() {
        viewModel.loadMemberList(args.studyId)
        viewModel.studyMemberListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                loadUserList(it)
            }
        )
    }

    private fun loadUserList(members: Map<String, Boolean>) {
        viewModel.loadUserList(args.studyId, members)
        viewModel.userListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                menuAdapter.submitList(it)
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