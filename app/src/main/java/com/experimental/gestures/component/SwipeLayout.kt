package com.experimental.gestures.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
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

class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private var childView: View? = null

    // Define initial state
    private var state = State.IDLE

    private var buttonWidthRatio = 0.3

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
        val event = resolveEvent(eventAction, eventX)
        Log.i(
            "TAG",
            "handleTouch: left:${childView?.left} right: ${childView?.right} x: ${childView?.x}"
        )
        when (state) {
            State.IDLE -> {
                reactIdle(event, eventX)
            }
            State.DRAGGING -> {
                reactDrag(event, eventX)
            }
            State.HOLDING_LEFT -> {
                reactHoldingLeft(event)
            }
            State.HOLDING_RIGHT -> {
                reactHoldingRight(event)
            }
        }
        return true
    }

    private fun reactIdle(event: Event, eventX: Float) {
        when (event) {
            Event.TOUCH_DOWN -> {
                initialXEvent = eventX
                transitTo(State.DRAGGING)
            }
            Event.TOUCH_UP -> Unit
            Event.DRAGGED -> Unit
            Event.SCROLLED -> Unit
            Event.VOID -> Unit
        }
    }

    private fun reactDrag(event: Event, eventX: Float) {
        when (event) {
            Event.TOUCH_DOWN -> Unit
            Event.TOUCH_UP -> {
                when {
                    this.x > this.width * buttonWidthRatio -> {
                        childView?.accelerateOriginHorizontally(
                            to = this.width * buttonWidthRatio
                        )
                        transitTo(State.HOLDING_LEFT)
                    }
                    this.x < -this.width * buttonWidthRatio -> {
                        childView?.accelerateOriginHorizontally(
                            to = -this.width * buttonWidthRatio
                        )
                        transitTo(State.HOLDING_RIGHT)
                    }
                    else -> {
                        transitTo(State.IDLE)
                        childView?.accelerateOriginHorizontally(to = 0)
                    }
                }
            }
            Event.DRAGGED -> {
                childView!!.x = eventX - initialXEvent
            }
            Event.VOID -> Unit
            Event.SCROLLED -> Unit
        }
    }

    private fun reactHoldingLeft(event: Event) {
        when (event) {
            Event.TOUCH_DOWN -> {}
            Event.TOUCH_UP -> {}
            Event.DRAGGED -> {}
            Event.VOID -> {}
            Event.SCROLLED -> {}
        }
    }

    private fun reactHoldingRight(event: Event) {
        when (event) {
            Event.TOUCH_DOWN -> {}
            Event.TOUCH_UP -> {}
            Event.DRAGGED -> {}
            Event.VOID -> {}
            Event.SCROLLED -> {}
        }
    }

    private fun transitTo(newState: State) {
        /*when(state){
            State.IDLE -> TODO()
            State.DRAGGING -> TODO()
            State.HOLDING_LEFT -> TODO()
            State.HOLDING_RIGHT -> TODO()
        }*/
        this.state = newState
    }


    // View.onGenericMotionEvent(MotionEvent)
    private fun resolveEvent(eventAction: Int, eventX: Float): Event {
        return when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
                Event.TOUCH_DOWN
            }
            MotionEvent.ACTION_UP -> {
                Event.TOUCH_UP
            }
            MotionEvent.ACTION_MOVE -> {
                Event.DRAGGED
            }
            else -> {
                Event.VOID
            }
        }
    }

    /*

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

    private fun returnToOriginalPosition() {
        childView?.accelerateOriginHorizontally(0)
    }
    * */

    enum class Event {
        TOUCH_DOWN,
        TOUCH_UP,
        DRAGGED,
        VOID,
        SCROLLED
    }

    enum class State {
        IDLE,// TODO ADD TRANSITING
        DRAGGING,
        HOLDING_LEFT,
        HOLDING_RIGHT
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
    to: Number,
    along: Long = 150,
    completion: (() -> Unit)? = null
) {
    val positionAnimator = ValueAnimator.ofFloat(this.x, to.toFloat())
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

/*fun Int.percent(percent: Int): Int {
    return this * percent / 100
}
 */