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
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.experimental.gestures.R


/**
 * Created by Brandon Quintanilla on Feb/24/2023
 */

class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : ConstraintLayout(ctx, attrs) {

    private var childView: View? = null

    // Define initial state
    private var state = State.IDLE

    private var buttonWidthRatio = 0.25

    private var childRelativeXEvent = 0f

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
        //addView(v,0)
        addSideChildren()
    }

    private fun addSideChildren() {
        // Create the left ImageView and set its image resource
        val leftImageView = ImageView(context)
        leftImageView.setBackgroundColor(context.getColor(R.color.green))
        leftImageView.id = View.generateViewId();
        //        leftImageView.setImageResource(R.drawable.left_image)

        // Create the right ImageView and set its image resource
        val rightImageView = ImageView(context)
        leftImageView.setBackgroundColor(context.getColor(R.color.red))
        rightImageView.id = View.generateViewId();
        //      rightImageView.setImageResource(R.drawable.right_image)

        // Add the left and right ImageViews to the ConstraintLayout
        this.addView(leftImageView)
        this.addView(rightImageView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        // Set the constraints for the left ImageView
        constraintSet.connect(
            leftImageView.id,
            ConstraintSet.START,
            this.id,
            ConstraintSet.START
        )
        constraintSet.connect(
            leftImageView.id,
            ConstraintSet.END,
            childView!!.id,
            ConstraintSet.START
        )
        constraintSet.connect(
            leftImageView.id,
            ConstraintSet.TOP,
            childView!!.id,
            ConstraintSet.TOP
        )
        constraintSet.connect(
            leftImageView.id,
            ConstraintSet.BOTTOM,
            childView!!.id,
            ConstraintSet.BOTTOM
        )

        // Set the constraints for the right ImageView
        constraintSet.connect(
            rightImageView.id,
            ConstraintSet.START,
            childView!!.id,
            ConstraintSet.END
        )
        constraintSet.connect(
            rightImageView.id,
            ConstraintSet.END,
            this.id,
            ConstraintSet.END
        )
        constraintSet.connect(
            rightImageView.id,
            ConstraintSet.TOP,
            childView!!.id,
            ConstraintSet.TOP
        )
        constraintSet.connect(
            rightImageView.id,
            ConstraintSet.BOTTOM,
            childView!!.id,
            ConstraintSet.BOTTOM
        )

        // Set the constraints for the existing child view
        constraintSet.connect(
            childView!!.id,
            ConstraintSet.END,
            leftImageView.id,
            ConstraintSet.START
        )

        // Apply the constraints to the ConstraintLayout
        constraintSet.applyTo(this)
    }

    private fun setup(
        context: Context, attrs: AttributeSet?
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
        typedArray.recycle()
        setOnTouchListener(buttonTouchListener)
    }

    private fun handleTouch(eventAction: Int, eventX: Float): Boolean {

        if (!childView!!.isTouchInside(eventX)) {
            return false
        }

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
                reactHoldingLeft(event, eventX)
            }
            State.HOLDING_RIGHT -> {
                reactHoldingRight(event, eventX)
            }
        }
        return true
    }

    private fun reactIdle(event: Event, eventX: Float) {
        when (event) {
            Event.TOUCH_DOWN -> {
                childRelativeXEvent = eventX
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
                resolveTransition()
            }
            Event.DRAGGED -> {
                childView!!.x = eventX - childRelativeXEvent
            }
            Event.VOID -> Unit
            Event.SCROLLED -> Unit
        }
    }

    private fun resolveTransition() {
        when {
            childView!!.x > this.width * 0.45 -> { // complete swipe to right and return
                childView?.accelerateOriginHorizontally(
                    to = this.width
                ) {
                    childView?.accelerateOriginHorizontally(to = 0)
                }
                transitTo(State.IDLE)
            }
            childView!!.x < -this.width * 0.45 -> { // complete swipe to left and return
                childView?.accelerateOriginHorizontally(
                    to = -this.width
                ) {
                    childView?.accelerateOriginHorizontally(to = 0)
                }
                transitTo(State.IDLE)
            }
            childView!!.x > this.width * buttonWidthRatio -> { // shows left
                childView?.accelerateOriginHorizontally(
                    to = this.width * buttonWidthRatio
                )
                transitTo(State.HOLDING_LEFT)
            }
            childView!!.x < -this.width * buttonWidthRatio -> { // shows right
                childView?.accelerateOriginHorizontally(
                    to = -this.width * buttonWidthRatio
                )
                transitTo(State.HOLDING_RIGHT)
            }
            else -> { // dropped at the middle -- action canceled
                transitTo(State.IDLE)
                childView?.accelerateOriginHorizontally(to = 0)
            }
        }
    }

    private fun reactHoldingLeft(event: Event, eventX: Float) {
        when (event) {
            Event.TOUCH_DOWN -> {
                childRelativeXEvent = (eventX - this.width * buttonWidthRatio).toFloat()
                transitTo(State.DRAGGING)
            }
            Event.TOUCH_UP -> Unit
            Event.DRAGGED -> Unit
            Event.VOID -> Unit
            Event.SCROLLED -> Unit
        }
    }

    private fun reactHoldingRight(event: Event, eventX: Float) {
        when (event) {
            Event.TOUCH_DOWN -> {
                childRelativeXEvent = (eventX + this.width * buttonWidthRatio).toFloat()
                transitTo(State.DRAGGING)
            }
            Event.TOUCH_UP -> Unit
            Event.DRAGGED -> Unit
            Event.VOID -> Unit
            Event.SCROLLED -> Unit
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

    enum class Event {
        TOUCH_DOWN, TOUCH_UP, DRAGGED, VOID, SCROLLED
    }

    enum class State {
        IDLE,// TODO ADD TRANSITING STATE
        DRAGGING, HOLDING_LEFT, HOLDING_RIGHT
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
    to: Number, along: Long = 125, completion: (() -> Unit)? = null
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

fun ImageView.width(width: Int) {
    val params = this.layoutParams
    params.width = width
    this.layoutParams = params
}