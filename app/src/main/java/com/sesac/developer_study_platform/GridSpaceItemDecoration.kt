package com.sesac.developer_study_platform

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val column = position % 2 + 1

        if (position < 2) {
            outRect.top = space
        } else {
            outRect.top = space / 2
        }
        if (column == 1) {
            outRect.left = space
            outRect.right = space / 2
        } else {
            outRect.right = space
            outRect.left = space / 2
        }
        outRect.bottom = space / 2
    }
}