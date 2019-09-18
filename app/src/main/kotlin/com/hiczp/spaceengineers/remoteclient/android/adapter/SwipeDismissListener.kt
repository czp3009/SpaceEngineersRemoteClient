package com.hiczp.spaceengineers.remoteclient.android.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * From https://github.com/lightningkite/kotlin-anko/blob/master/src/main/java/com/lightningkite/kotlin/anko/adapter/SwipeDismissListener.kt
 * Created by jivie on 2/11/16.
 */
class SwipeDismissListener(
    val canDismiss: (Int) -> Boolean = { true },
    val action: (Int) -> Unit
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
        makeMovementFlags(
            0,
            if (canDismiss(viewHolder.adapterPosition)) ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT else 0
        )

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
            action(viewHolder.adapterPosition)
        }
    }
}

fun RecyclerView.swipeToDismiss(
    canDismiss: (Int) -> Boolean = { true },
    action: (Int) -> Unit
) {
    val listener = SwipeDismissListener(canDismiss, action)
    ItemTouchHelper(listener).attachToRecyclerView(this)
}
