package com.sesac.developer_study_platform.ui.mypage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator : DayViewDecorator {

    private val today = CalendarDay.today()
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == today
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(object : LineBackgroundSpan {
            override fun drawBackground(
                canvas: Canvas,
                paint: Paint,
                left: Int,
                right: Int,
                top: Int,
                baseline: Int,
                bottom: Int,
                charSequence: CharSequence,
                start: Int,
                end: Int,
                lineNum: Int
            ) {
                val radius = (bottom - top) / 1
                val centerX = (left + right) / 2
                val centerY = (top + bottom) / 2
                canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), this@TodayDecorator.paint)
            }
        })
    }
}