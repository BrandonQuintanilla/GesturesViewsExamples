package com.experimental.gestures

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Brandon Quintanilla on Feb/22/2023
 */

//Posible eventos (inputs) drag(vector.x), down, up(where to identify new state)
//                 -> HOLDING_LEFT
//IDLE -> DRAGGING -> DRAGGING
//                 -> HOLDING_RIGHT
enum class ITEM_STATE {
    IDLE, // The item is not touched
    DRAGGING, // The item is been touched (puede pasar de dragging to dragging pero actualiza la posicion)
    HOLDING_LEFT, // The left icon is visible
    HOLDING_RIGHT // The right icon is visible
}

//TODO replace it by  custom Layout
class CustomSwipeGesture(val mAdapter: SwipeAdapter) :
    ItemTouchHelper
    .Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        TODO("Not yet implemented")
    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        mAdapter.deleteItem(position)
    }
}