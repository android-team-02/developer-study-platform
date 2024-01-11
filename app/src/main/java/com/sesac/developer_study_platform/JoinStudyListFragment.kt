package com.sesac.developer_study_platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class JoinStudyListFragment : Fragment() {

    private lateinit var adapter: JoinStudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_study_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_study)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = JoinStudyAdapter()
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : JoinStudyAdapter.OnItemClickListener {
            override fun onItemClick(study: Study) {
                val action = JoinStudyListFragmentDirections
                    .actionJoinStudyListFragmentToNextFragment()
                findNavController().navigate(action)
            }
        })

        view.findViewById<ShapeableImageView>(R.id.iv_arrow).setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tempData = listOf(
            Study(
            banList = listOf("dfasf", "adfas", "afdalf"),
            category = "Android",
            chatroomId = 1,
            content = "안드로이드 취업을 목표로 하고있습니다. 스터디 마지막에는 협업 프로젝트를 할 계획을 가지고 있어요.",
            createdDate = "20240109",
            currentPeople = 4,
            dayList = DayList(day = listOf("월", "1223", "1231")),
            endDate = "20240210",
            id = 1,
            image = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_1280.jpg",
            language = "Kotlin",
            name = "예비 안드로이드 개발자를 위한 스터디",
            startDate = "20240109",
            totalPeople = 8,
            userIdList = listOf("YuGyeong98", "0se0", "nuyhus-m", "AnMyungwoo94")
        ),
            Study(
                banList = listOf("dfasf", "adfas", "afdalf"),
                category = "Android",
                chatroomId = 1,
                content = "안드로이드 취업을 목표로 하고있습니다. 스터디 마지막에는 협업 프로젝트를 할 계획을 가지고 있어요.",
                createdDate = "20240109",
                currentPeople = 8,
                dayList = DayList(day = listOf("일", "1223", "1231")),
                endDate = "20240210",
                id = 1,
                image = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_1280.jpg",
                language = "Java, Spring",
                name = "예비 백엔드 개발자를 위한 스터디",
                startDate = "20240108",
                totalPeople = 8,
                userIdList = listOf("YuGyeong98", "0se0", "nuyhus-m", "AnMyungwoo94")
            )
        )
        tempData = tempData.sortedBy { it.startDate }
        adapter.setData(tempData)
    }
}