package com.experimental.gestures.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Brandon Quintanilla on Feb/22/2023
 */

//Posible eventos (inputs) drag(vector.x), down, up(where to identify new state)
//                                 -> DRAGGING
//                 -> HOLDING_LEFT -> RETURNING_LEFT_IDLE -> IDLE
//IDLE -> DRAGGING -> DRAGGING
//                 -> IDLE
//                 -> HOLDING_RIGHT -> RETURNING_RIGHT_IDLE -> IDLE
//                                  -> DRAGGING
enum class ITEM_STATE {
    IDLE, // The item is not touched
    DRAGGING, // The item is been touched (puede pasar de dragging to dragging pero actualiza la posicion)
    // De DRAGGING a HOLDING____ no se reaccionarán a eventos de swipe
    HOLDING_LEFT, // The left icon is visible
    HOLDING_RIGHT, // The right icon is visible
    RETURNING_LEFT, // defines direction and handle cancel animation
    RETURNING_RIGHT // defines direction and handle cancel animation
}

class CustomSwipeGesture @JvmOverloads constructor(ctx: Context, attrs: AttributeSet) :
    ConstraintLayout(ctx, attrs) {

    fun isSwipeEnabled() = true


    fun onSwiped(position: Int) {

    }
}