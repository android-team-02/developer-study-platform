package com.sesac.developer_study_platform.ui.mypage

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class DotSpanDecorator(
    days: Collection<CalendarDay>
) : DayViewDecorator {

    private val daysSet = HashSet(days)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return daysSet.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val dotRadius = 5f
        view.addSpan(DotSpan(dotRadius, Color.BLACK))
    }
}