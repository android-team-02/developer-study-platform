package com.sesac.developer_study_platform.data.source.local

import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkDao
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.data.Study

class BookmarkRepository {

    suspend fun isBookmarkSelected(sid: String): Boolean {
        return bookmarkDao.getBookmarkStudyBySid(sid).isNotEmpty()
    }

    suspend fun insertBookmarkStudy(study: Study) {
        bookmarkDao.insertBookmarkStudy(
            BookmarkStudy(
                study.sid,
                study.name,
                study.image,
                study.language,
                study.days.joinToString(", ") { it.split(" ").first() }
            )
        )
    }

    suspend fun deleteBookmarkStudyBySid(sid: String) {
        bookmarkDao.deleteBookmarkStudyBySid(sid)
    }
}