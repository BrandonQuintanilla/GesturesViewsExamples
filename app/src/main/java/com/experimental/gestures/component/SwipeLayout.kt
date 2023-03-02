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
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.experimental.gestures.R


/**
 * Created by Brandon Quintanilla on Feb/24/2023
 */
//TODO handle scroll
//TODO handle other action events (VOID INTERACTION)
//TODO ADD side and child view overlap
class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private var state = State.IDLE

    private var buttonWidthRatio = 0.25
    private var acceptSwipeThreshold = 0.45

    private var childView: View? = null
    private var childAnchorPosition = 0f

    private var onSwipeListener: ((Int) -> Unit?) = {}
    private var onOpenStarted: (() -> Unit?) = {}

    companion object {
        const val RIGHT = 1
        const val LEFT = 1
    }

    private var previousMotionEvent: Int = -1

    private var leftImageView = ImageView(ctx)
    private var rightImageView = ImageView(ctx)

    init {
        setup(ctx, attrs)
    }

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

        previousMotionEvent = event?.action ?: -1
        return state == State.DOWN
    }

    /***
     * The performClick event is executed when the DOWN state interacts with a UP action
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        previousMotionEvent = event?.action ?: -1
        event?.let {
            handleTouch(event.action, event.x)
        }

        return true
    }

    fun closeProgrammatically() {
        if (state != State.DRAGGING) {
            returnToIdle()
        }
    }

    private fun returnToIdle() {
        transitTo(State.IDLE)
        acceleratedInterpolation(
            from = childView!!.x,
            to = 0,
            onValue = {
                childView!!.x = it.toFloat()
                leftImageView.width(it)
                updateRightViewParams()
            })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        childView = children.single()
        addSideChildren()
    }

    private fun addSideChildren() {

        leftImageView.setBackgroundColor(context.getColor(R.color.green))
        leftImageView.layoutParams = FrameLayout.LayoutParams(0, 0)
        leftImageView.id = View.generateViewId()

        rightImageView.setBackgroundColor(context.getColor(R.color.red))
        rightImageView.layoutParams = FrameLayout.LayoutParams(0, 0)
        rightImageView.id = View.generateViewId()

        this.addView(leftImageView, 0)
        this.addView(rightImageView, 0)
    }

    private fun handleTouch(eventAction: Int, eventX: Float) {

        if (!childView!!.isTouchInside(eventX)) {
            return
        }

        val interaction = resolveInteraction(eventAction, eventX)
        Log.i("interaction", "handleTouch II: $interaction $state  $eventAction")
        when (state) {
            State.IDLE -> {
                reactIdle(interaction, eventX)
            }
            State.DOWN -> {
                reactIDown(interaction, eventX)
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
        Log.i("interaction", "handleTouch FF: $interaction $state")
        return
    }

    var lastX = -1f

    private fun reactIDown(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                lastX = eventX
            }
            Interaction.TOUCH_UP -> {
                this.performClick()
                transitTo(State.IDLE)
            }
            Interaction.DRAGGED -> {
                val deltaX = lastX - eventX
                lastX = eventX
                if (-0.0001f < deltaX && deltaX < 0.0001f) {
                    transitTo(State.IDLE)
                } else {
                    transitTo(State.DRAGGING)
                    this.onOpenStarted.invoke()
                }
            }
            Interaction.SCROLLED -> Unit
            Interaction.VOID -> Unit
        }
    }

    private fun reactIdle(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                childAnchorPosition = eventX
                transitTo(State.DOWN)
            }
            Interaction.TOUCH_UP -> Unit
            Interaction.DRAGGED -> Unit
            Interaction.SCROLLED -> Unit
            Interaction.VOID -> Unit
        }
    }

    fun onLeftClick(l: OnClickListener) {
        this.leftImageView.setOnClickListener {
            completeRightSwipe()
            l.onClick(it)
        }
    }

    fun onRightClick(l: OnClickListener) {
        this.rightImageView.setOnClickListener {
            completeLeftSwipe()
            l.onClick(it)
        }
    }

    fun onSwipe(l: (Int) -> Unit) {
        this.onSwipeListener = l
    }

    fun onOpenStarted(l: () -> Unit) {
        this.onOpenStarted = l
    }

    private fun reactDrag(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> Unit
            Interaction.TOUCH_UP -> {
                resolveTransition()
            }
            Interaction.DRAGGED -> {
                childView!!.x = eventX - childAnchorPosition
                updateLeftViewParams()
                updateRightViewParams()
            }
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
        }
    }

    private fun updateLeftViewParams() {
        leftImageView.layoutParams = FrameLayout.LayoutParams(
            childView!!.x.toInt(), childView!!.height
        )
    }

    private fun updateRightViewParams() {
        rightImageView.x = this.width.toFloat() + childView!!.x.toInt()
        rightImageView.layoutParams = FrameLayout.LayoutParams(
            -childView!!.x.toInt(), childView!!.height
        )
    }

    private fun resolveTransition() {
        when {
            childView!!.x > this.width * acceptSwipeThreshold -> { // complete swipe to right and return
                completeRightSwipe()
                transitTo(State.IDLE)
                onSwipeListener.invoke(RIGHT)
            }
            childView!!.x < -this.width * acceptSwipeThreshold -> { // complete swipe to left and return
                completeLeftSwipe()
                transitTo(State.IDLE)
                onSwipeListener.invoke(LEFT)
            }
            childView!!.x > this.width * buttonWidthRatio -> { // shows left
                acceleratedInterpolation(
                    from = childView!!.x,
                    to = this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    })
                transitTo(State.HOLDING_LEFT)
            }
            childView!!.x < -this.width * buttonWidthRatio -> { // shows right
                acceleratedInterpolation(
                    from = childView!!.x,
                    to = -this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        updateRightViewParams()
                    })
                transitTo(State.HOLDING_RIGHT)
            }
            else -> { // dropped at the middle -- action canceled
                returnToIdle()
            }
        }
    }

    private fun completeLeftSwipe() {
        acceleratedInterpolation(from = childView!!.x, to = -this.width, onValue = {
            childView!!.x = it.toFloat()
            updateRightViewParams()
        }) {
            acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                childView!!.x = it.toFloat()
                updateRightViewParams()
            })
        }
    }

    private fun completeRightSwipe() {
        acceleratedInterpolation(from = childView!!.x, to = this.width, onValue = {
            childView!!.x = it.toFloat()
            leftImageView.width(it)
        }) {
            acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                childView!!.x = it.toFloat()
                leftImageView.width(it)
            })
        }
    }

    private fun reactHoldingLeft(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> {
                childAnchorPosition = (eventX - this.width * buttonWidthRatio).toFloat()
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
                childAnchorPosition = (eventX + this.width * buttonWidthRatio).toFloat()
                transitTo(State.DRAGGING)
            }
            Interaction.TOUCH_UP -> Unit
            Interaction.DRAGGED -> Unit
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
        }
    }

    private fun transitTo(newState: State) {
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
            MotionEvent.ACTION_CANCEL -> {
                Interaction.TOUCH_UP
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
        IDLE,
        DOWN,
        DRAGGING,
        HOLDING_LEFT,
        HOLDING_RIGHT
    }
}

//////////////////////////////
////////////Tools/////////////
//////////////////////////////

fun View.isTouchInside(eventX: Float): Boolean {
    val isBeforeLeft = eventX <= this.x + this.width
    val isAfterRight = eventX > this.x
    return isBeforeLeft && isAfterRight
}

fun ImageView.width(width: Number) {
    this.layoutParams.let {
        it.width = width.toInt()
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