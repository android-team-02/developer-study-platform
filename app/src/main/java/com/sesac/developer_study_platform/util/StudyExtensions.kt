package com.sesac.developer_study_platform.util

import com.sesac.developer_study_platform.data.Study

fun List<Study>.sortStudyList(): List<Study> {
    return this.sortedBy {
        val comparator = it.totalMemberCount - it.members.count()
        if (comparator != 0) comparator else 8
    }
}