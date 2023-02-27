package com.experimental.gestures.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.experimental.gestures.R


/**
 * Created by Brandon Quintanilla on Feb/24/2023
 */

class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private var childView: View? = null

    private var state = State.IDLE

    private var buttonWidthRatio = 0.25

    private var childRelativeXEvent = 0f

    private var onSwipeListener: ((Int) -> Unit?) = {}

    companion object {
        const val RIGHT = 1
        const val LEFT = 1
    }

    private var previousMotionEvent: Int = -1

    init {
        setup(ctx, attrs)
    }

    private var leftImageView = ImageView(ctx)
    private var rightImageView = ImageView(ctx)

    private fun setup(
        context: Context, attrs: AttributeSet?
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
        typedArray.recycle()
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {

        event?.let {
            handleTouch(event.action, event.x)
        }

        // If we return true, the child is not notified of (UP)event and click is not executed
        // So we don´t want to execute onClick on child when we just dragged the view
        if (event?.action == MotionEvent.ACTION_UP && previousMotionEvent != MotionEvent.ACTION_DOWN) {
            return true
        }

        previousMotionEvent = event?.action ?: -1
        return super.onInterceptTouchEvent(event)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        childView = children.single()
        addSideChildren()
    }

    private fun addSideChildren() {

        // Create the left ImageView and set its image resource
        leftImageView.setBackgroundColor(context.getColor(R.color.green))
        leftImageView.layoutParams = FrameLayout.LayoutParams(0, 0)
        leftImageView.id = View.generateViewId()

        // Create the right ImageView and set its image resource
        //TODO rightImageView is not modified by dragging
        rightImageView.setBackgroundColor(context.getColor(R.color.red))
        rightImageView.layoutParams = FrameLayout.LayoutParams(0, 0)
        rightImageView.id = View.generateViewId()

        // Add the left and right ImageViews to the ConstraintLayout
        this.addView(leftImageView, 0)
        this.addView(rightImageView, 0)
    }

    private fun handleTouch(eventAction: Int, eventX: Float): Boolean {

        if (!childView!!.isTouchInside(eventX)) {
            return false
        }

        val interaction = resolveInteraction(eventAction, eventX)
        when (state) {
            State.IDLE -> {
                reactIdle(interaction, eventX)
            }
            State.DRAGGING -> {
                reactDrag(interaction, eventX)
            }
            State.HOLDING_LEFT -> {
                reactHoldingLeft(interaction, eventX)
            }
            State.HOLDING_RIGHT -> {
                reactHoldingRight(interaction, eventX)
            }
        }
        return true
    }

    private fun reactIdle(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                childRelativeXEvent = eventX
                transitTo(State.DRAGGING)
            }
            Interaction.TOUCH_UP -> Unit
            Interaction.DRAGGED -> Unit
            Interaction.SCROLLED -> Unit
            Interaction.VOID -> Unit
        }
    }

    fun onLeftClick(l: OnClickListener) = this.leftImageView.setOnClickListener(l)
    fun onRightClick(l: OnClickListener) = this.rightImageView.setOnClickListener(l)

    fun onSwipe(l: (Int) -> Unit) {
        this.onSwipeListener = l
    }

    private fun reactDrag(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> Unit
            Interaction.TOUCH_UP -> {
                resolveTransition()
            }
            Interaction.DRAGGED -> {
                childView!!.x = eventX - childRelativeXEvent
                setTrailingEffect()
                setLeadingEffect()
            }
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
        }
    }

    //TODO rename
    private fun setTrailingEffect() {
        leftImageView.layoutParams = FrameLayout.LayoutParams(
            childView!!.x.toInt(), childView!!.height
        )
    }

    // TODO optimize
    private fun setLeadingEffect() {
        rightImageView.x = this.width.toFloat() + childView!!.x.toInt()
        rightImageView.layoutParams = FrameLayout.LayoutParams(
            -childView!!.x.toInt(), childView!!.height
        )
    }

    private fun resolveTransition() {
        when {
            childView!!.x > this.width * 0.45 -> { // complete swipe to right and return
                acceleratedInterpolation(from = childView!!.x, to = this.width, onValue = {
                    childView!!.x = it.toFloat()
                    leftImageView.width(it)
                }) {
                    acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    })
                }
                transitTo(State.IDLE)
                onSwipeListener.invoke(RIGHT)
            }
            childView!!.x < -this.width * 0.45 -> { // complete swipe to left and return
                acceleratedInterpolation(from = childView!!.x, to = -this.width, onValue = {
                    childView!!.x = it.toFloat()
                    setLeadingEffect()
                }) {
                    acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                        childView!!.x = it.toFloat()
                        setLeadingEffect()
                    })
                }
                transitTo(State.IDLE)
                onSwipeListener.invoke(LEFT)
            }
            childView!!.x > this.width * buttonWidthRatio -> { // shows left
                acceleratedInterpolation(from = childView!!.x,
                    to = this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    })
                transitTo(State.HOLDING_LEFT)
            }
            childView!!.x < -this.width * buttonWidthRatio -> { // shows right
                acceleratedInterpolation(from = childView!!.x,
                    to = -this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        setLeadingEffect()
                    })
                transitTo(State.HOLDING_RIGHT)
            }
            else -> { // dropped at the middle -- action canceled
                transitTo(State.IDLE)
                acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                    childView!!.x = it.toFloat()
                    leftImageView.width(it)
                })
            }
        }
    }

    private fun reactHoldingLeft(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                childRelativeXEvent = (eventX - this.width * buttonWidthRatio).toFloat()
                transitTo(State.DRAGGING)
            }
            Interaction.TOUCH_UP -> Unit
            Interaction.DRAGGED -> Unit
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
        }
    }

    private fun reactHoldingRight(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                childRelativeXEvent = (eventX + this.width * buttonWidthRatio).toFloat()
                transitTo(State.DRAGGING)
            }
            Interaction.TOUCH_UP -> Unit
            Interaction.DRAGGED -> Unit
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
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
    private fun resolveInteraction(eventAction: Int, eventX: Float): Interaction {
        return when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
                Interaction.TOUCH_DOWN
            }
            MotionEvent.ACTION_UP -> {
                Interaction.TOUCH_UP
            }
            MotionEvent.ACTION_MOVE -> {
                Interaction.DRAGGED
            }
            else -> {
                Interaction.VOID
            }
        }
    }

    enum class Interaction {
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
    this.layoutParams.let {
        it.width = width
        this.layoutParams = it
    }
}

inline fun acceleratedInterpolation(
    from: Number,
    to: Number,
    along: Long = 125,
    crossinline onValue: ((Int) -> Unit),
    noinline completion: (() -> Unit)? = null
) {
    val positionAnimator = ValueAnimator.ofInt(from.toInt(), to.toInt())
    positionAnimator.interpolator = AccelerateDecelerateInterpolator()
    positionAnimator.addUpdateListener {
        onValue.invoke(it.animatedValue as Int)
    }
    completion?.let {
        positionAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                completion.invoke()
            }
        })
    }
    positionAnimator.duration = along
    positionAnimator.start()
}