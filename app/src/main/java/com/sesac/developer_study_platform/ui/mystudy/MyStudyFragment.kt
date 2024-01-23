package com.sesac.developer_study_platform.ui.mystudy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMyStudyBinding
import kotlinx.coroutines.launch

class MyStudyFragment : Fragment() {

    private var _binding: FragmentMyStudyBinding? = null
    private val binding get() = _binding!!
    private val myStudyAdapter = MyStudyAdapter { userStudyRoom ->
        val action = MyStudyFragmentDirections.actionMyStudyToDetail(userStudyRoom.sid)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvStudy.adapter = myStudyAdapter
        loadStudyList()
    }

    private fun loadStudyList() {
        val service = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                service.getUserStudyList(Firebase.auth.uid)
            }.onSuccess { map ->
                val studyRooms = map.values.map { userStudyRoom ->
                    UserStudy(
                        days = userStudyRoom.days,
                        image = userStudyRoom.image,
                        language = userStudyRoom.language,
                        name = userStudyRoom.name,
                        sid = userStudyRoom.sid
                    )
                }
                setStudyList(studyRooms)
            }.onFailure { exception ->
                Log.e(
                    "JoinStudyListFragment",
                    "Failed to load user study rooms: ${exception.message}"
                )
            }
        }
    }

    private fun setStudyList(studyRooms: List<UserStudy>) {
        myStudyAdapter.submitList(studyRooms)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
