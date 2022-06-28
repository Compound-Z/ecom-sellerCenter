package vn.ztech.software.ecomSeller.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationRecyclerViewPadding() :
	RecyclerView.ItemDecoration() {
	private val paddingSpace = 16

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		super.getItemOffsets(outRect, view, parent, state)
		outRect.set(paddingSpace, paddingSpace, paddingSpace, paddingSpace)
	}
}