package org.mozilla.focus.session.ui

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.graphics.drawable.ColorDrawable
import android.support.v7.content.res.AppCompatResources
import android.graphics.Color

class SessionsAdapterTouchHelperCallback(
    private val mAdapter: SessionsAdapter,
    context: Context?
) : ItemTouchHelper.Callback() {
    companion object {
        private const val BACKGROUND_CORNER_OFFSET = 20
        private val background = ColorDrawable(Color.RED)
    }

    private val icon = context?.let { AppCompatResources.getDrawable(it, org.mozilla.focus.R.drawable.ic_delete) }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        //We support only dismiss (swipe right) for session view holders.
        val swipeable = viewHolder is SessionViewHolder
        return makeMovementFlags(0, if (swipeable) ItemTouchHelper.RIGHT else 0)
    }

    /**
     * No need to support reordering
     */
    override fun onMove(recyclerView: RecyclerView, dragged: ViewHolder, target: ViewHolder): Boolean {
        return false
    }

    /**
     * When the item is swiped right, we will draw a background plus the erase icon on
     * the empty space behind to indicate that the swipe will result in deleting the tab (session)
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        if (dX > 0) { // Swiping to the right
            icon?.let { icon ->
                //Ignore the icon if we couldn't load it
                val iconHeight = icon.intrinsicHeight
                val iconWidth = icon.intrinsicWidth

                val iconMargin = (itemView.height - iconHeight) / 2
                val iconTop = itemView.top + (itemView.height - iconHeight) / 2
                val iconBottom = iconTop + iconHeight

                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + iconWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }

            background.setBounds(itemView.left, itemView.top,
                itemView.left + dX.toInt() + BACKGROUND_CORNER_OFFSET,
                itemView.bottom)
        } else { // swipe released
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon?.draw(c)
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }
}
