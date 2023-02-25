package com.experimental.gestures.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.children
import com.experimental.gestures.R

/**
 * Created by Brandon Quintanilla on Feb/24/2023
 */
enum class Event {
    TOUCH_DOWN,
    TOUCH_UP,
}

enum class State {
    IDLE, DRAGGING, HOLDING_LEFT, HOLDING_RIGHT
}

class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private var childView: View? = null

    private var initialXEvent = 0f

    private var onActiveListener: OnActiveListener? = null

    @SuppressLint("ClickableViewAccessibility")
    private val buttonTouchListener: OnTouchListener = OnTouchListener { v, event ->
        handleTouch(event.action, event.x)
    }

    init {
        setup(ctx, attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        childView = children.single()
    }

    private fun setup(
        context: Context, attrs: AttributeSet?
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
        typedArray.recycle()
        setOnTouchListener(buttonTouchListener)
    }

    private fun handleTouch(eventAction: Int, eventX: Float): Boolean {
        return when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
                initialXEvent = eventX
                childView?.isTouchInside(eventX) ?: false
            }
            MotionEvent.ACTION_MOVE -> {

                val deltaX = initialXEvent - eventX
                //attach to touch point
                childView!!.x = eventX - initialXEvent


                /*
                if (deltaX > 0) {
                    // The touch move was to the right
                    // Do something here...
                } else if (deltaX < 0) {
                    // The touch move was to the left
                    // Do something here...
                }*/

                true
            }
            MotionEvent.ACTION_UP -> {

                //resolveRelativePosition()
                if (childView!!.x + childView!!.width > width * 0.9) {
                    onActiveListener?.onActive()
                }
                returnToOriginalPosition()

                true
            }
            else -> false
        }
    }

    private fun returnToOriginalPosition() {
        childView?.accelerateOriginHorizontally() {
            //initialXEvent = 0f
        }
    }
}

/////////////////////////////
//////////Extensions/////////
/////////////////////////////

fun View.isTouchInside(eventX: Float): Boolean {
    val isBeforeLeft = eventX <= this.x + this.width
    val isAfterRight = eventX > this.x
    return isBeforeLeft && isAfterRight
}

fun View.accelerateOriginHorizontally(
    to: Float = 0f,
    along: Long = 150,
    completion: (() -> Unit)? = null
) {
    val positionAnimator = ValueAnimator.ofFloat(this.x, to)
    positionAnimator.interpolator = AccelerateDecelerateInterpolator()
    positionAnimator.addUpdateListener {
        val x = positionAnimator.animatedValue as Float
        this.x = x
    }
    positionAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            completion?.invoke()
        }
    })
    positionAnimator.duration = along
    positionAnimator.start()
}