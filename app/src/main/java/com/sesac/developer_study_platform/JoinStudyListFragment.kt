package com.sesac.developer_study_platform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.databinding.FragmentJoinStudyListBinding
import kotlinx.coroutines.launch

class JoinStudyListFragment : Fragment() {

    private var _binding: FragmentJoinStudyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var joinStudyAdapter: JoinStudyAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinStudyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        joinStudyAdapter = JoinStudyAdapter { userStudyRoom ->
            val action = JoinStudyListFragmentDirections
                .actionJoinStudyListFragmentToDetail(userStudyRoom.sid)
            findNavController().navigate(action)
        }

        binding.rvStudy.apply {
            adapter = joinStudyAdapter
        }

        loadStudyList()
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getStudyList("abcd")
            }.onSuccess { map ->
                val studyRooms = map.values.map { userStudyRoom ->
                    UserStudyRoom(
                        days = userStudyRoom.days,
                        image = userStudyRoom.image,
                        language = userStudyRoom.language,
                        name = userStudyRoom.name,
                        sid = userStudyRoom.sid
                    )
                }
                setStudyList(studyRooms)
            }.onFailure { exception ->
                Log.e("JoinStudyListFragment", "Failed to load user study rooms: ${exception.message}")
            }
        }
    }

    private fun setStudyList(studyRooms: List<UserStudyRoom>) {
        joinStudyAdapter.submitList(studyRooms)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
