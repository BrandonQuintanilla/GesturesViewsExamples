package com.experimental.gestures

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation

import androidx.dynamicanimation.animation.SpringAnimation
import kotlin.math.abs


/**
 * Created by Brandon Quintanilla on Feb/23/2023
 */

enum class CustomViewEvent {
    TOUCH_DOWN,
    TOUCH_UP,
    RETURNED_TO_IDLE
}

enum class CustomViewState {
    IDLE,
    DRAGGING,
    RETURNING
}

class DraggableBouncingView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private enum class CustomViewState {
        IDLE, DRAGGING, RETURNING
    }

    private var state = CustomViewState.IDLE
    private var lastX = 0f
    private var lastY = 0f
    private val stiffness = 1000.0f
    private val dampingRatio = 0.7f
    private val dragThreshold = 10.0f
    private var translationX = 0.0f
    private var translationY = 0.0f
    private val springAnimationX: SpringAnimation
    private val springAnimationY: SpringAnimation

    init {
        springAnimationX = SpringAnimation(this, DynamicAnimation.TRANSLATION_X, 0.0f)
        springAnimationY = SpringAnimation(this, DynamicAnimation.TRANSLATION_Y, 0.0f)
        springAnimationX.spring.stiffness = stiffness
        springAnimationY.spring.stiffness = stiffness
        springAnimationX.spring.dampingRatio = dampingRatio
        springAnimationY.spring.dampingRatio = dampingRatio
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (state == CustomViewState.RETURNING) {
            springAnimationX.start()
            springAnimationY.start()
            if (springAnimationX.isRunning || springAnimationY.isRunning) {
                invalidate()
            } else {
                translationX = 0.0f
                translationY = 0.0f
                state = CustomViewState.IDLE
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                state = CustomViewState.DRAGGING
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastX
                val deltaY = y - lastY
                if (state == CustomViewState.DRAGGING &&
                    (abs(deltaX) > dragThreshold || abs(deltaY) > dragThreshold)
                ) {
                    translationX += deltaX
                    translationY += deltaY
                    setTranslationX(translationX)
                    setTranslationY(translationY)
                    lastX = x
                    lastY = y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> if (state == CustomViewState.DRAGGING) {
                state = CustomViewState.RETURNING
                invalidate()
            }
        }
        return true
    }
}