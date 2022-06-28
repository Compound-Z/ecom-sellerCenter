package vn.ztech.software.ecomSeller.ui.product_details

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class DotsIndicatorDecoration(
    private val radius: Float,
    private val indicatorItemPadding: Float,
    private val indicatorHeight: Int,
    @ColorInt private val colorInactive: Int,
    @ColorInt private val colorActive: Int
) : RecyclerView.ItemDecoration() {

    private val inactivePaint = Paint()
    private val activePaint = Paint()

    init {
        val width = Resources.getSystem().displayMetrics.density * 1
        inactivePaint.apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = width
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = colorInactive
        }

        activePaint.apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = width
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            color = colorActive
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val adapter = parent.adapter ?: return

        val itemCount = adapter.itemCount

        val totalLength: Float = (radius * 2 * itemCount)
        val padBWItems = max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + padBWItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2F

        val indicatorPosY = parent.height - indicatorHeight / 2F

        drawInactiveDots(c, indicatorStartX, indicatorPosY, itemCount)

        val activePos: Int =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (activePos == RecyclerView.NO_POSITION) {
            return
        }

        val activeChild =
            (parent.layoutManager as LinearLayoutManager).findViewByPosition(activePos)
                ?: return

        drawActiveDot(c, indicatorStartX, indicatorPosY, activePos)


    }

    private fun drawInactiveDots(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        val w = radius * 2 + indicatorItemPadding
        var st = indicatorStartX + radius
        for (i in 1..itemCount) {
            c.drawCircle(st, indicatorPosY, radius, inactivePaint)
            st += w
        }
    }

    private fun drawActiveDot(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        highlightPos: Int
    ) {
        val w = radius * 2 + indicatorItemPadding
        val highStart = indicatorStartX + radius + w * highlightPos
        c.drawCircle(highStart, indicatorPosY, radius, activePaint)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }

}