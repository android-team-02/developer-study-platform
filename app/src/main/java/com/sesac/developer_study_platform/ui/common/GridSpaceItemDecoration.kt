package com.sesac.developer_study_platform.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager = parent.layoutManager as GridLayoutManager
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2 + 1
        val itemCount = state.itemCount
        val totalRowCount = if (itemCount % 2 == 0) {
            itemCount / 2
        } else {
            itemCount / 2 + 1
        }
        val currentRow = layoutManager.spanSizeLookup.getSpanGroupIndex(position, 2)

        if (position < 2) {
            outRect.top = space
        } else {
            outRect.top = space / 2
        }

        if (column == 1) {
            outRect.left = space
            outRect.right = space / 4
        } else {
            outRect.left = space / 4
            outRect.right = space
        }

        if (currentRow == totalRowCount - 1) {
            outRect.bottom = space
        }
    }
}