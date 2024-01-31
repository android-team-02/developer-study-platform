package com.sesac.developer_study_platform.ui.mypage

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class DotSpanDecorator(
    private val color: Int,
    days: Collection<CalendarDay>
) : DayViewDecorator {

    private val daysSet = HashSet(days)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return daysSet.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color))
    }
}