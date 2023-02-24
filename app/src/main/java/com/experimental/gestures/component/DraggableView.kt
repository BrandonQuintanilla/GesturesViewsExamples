package com.experimental.gestures.component

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Brandon Quintanilla on Feb/23/2023
 */
enum class DraggableViewEvent {
    TOUCH_DOWN,
    TOUCH_UP
}

enum class DraggableViewState {
    IDLE,
    DRAGGING
}

class DraggableView(context: Context, attrs: AttributeSet) : View(context, attrs),
    View.OnTouchListener {

    // Define initial state
    private var state = DraggableViewState.IDLE

    // Properties to track last known touch event coordinates
    private var lastX: Float = 0f
    private var lastY: Float = 0f

    init {
        // Set the view's onTouchListener to this instance
        setOnTouchListener(this)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (state == DraggableViewState.IDLE) {
                    transition(DraggableViewEvent.TOUCH_DOWN)
                    lastX = event.rawX
                    lastY = event.rawY
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (state == DraggableViewState.DRAGGING) {
                    transition(DraggableViewEvent.TOUCH_UP)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == DraggableViewState.DRAGGING) {
                    val deltaX = event.rawX - lastX
                    val deltaY = event.rawY - lastY
                    translationX += deltaX
                    translationY += deltaY
                    lastX = event.rawX
                    lastY = event.rawY
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun transition(event: DraggableViewEvent) {
        state = when (state) {
            DraggableViewState.IDLE -> {
                when (event) {
                    DraggableViewEvent.TOUCH_DOWN -> {
                        // Start dragging
                        DraggableViewState.DRAGGING
                    }
                    else -> state
                }
            }
            DraggableViewState.DRAGGING -> {
                when (event) {
                    DraggableViewEvent.TOUCH_UP -> {
                        // Stop dragging
                        DraggableViewState.IDLE
                    }
                    else -> state
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the custom view contents here
    }
}
