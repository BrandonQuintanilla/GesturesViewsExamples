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
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
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
        addSideChildren()
    }

    private lateinit var leftImageView: ImageView
    private lateinit var rightImageView: ImageView
    private fun addSideChildren() {

        // Create the left ImageView and set its image resource
        leftImageView = ImageView(context)
        leftImageView.setBackgroundColor(context.getColor(R.color.green))
        leftImageView.id = View.generateViewId()

        // Create the right ImageView and set its image resource
        //TODO rightImageView is not modified by dragging
        rightImageView = ImageView(context)
        rightImageView.setBackgroundColor(context.getColor(R.color.red))
        rightImageView.id = View.generateViewId()

        // Add the left and right ImageViews to the ConstraintLayout
        this.addView(leftImageView, 0)
        this.addView(rightImageView, 0)
    }

    private fun setup(
        context: Context, attrs: AttributeSet?
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
        typedArray.recycle()
        setOnTouchListener(buttonTouchListener)
        //addSideChildren()
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



    private fun reactDrag(interaction: Interaction, eventX: Float) {
        when (interaction) {
            Interaction.TOUCH_DOWN -> Unit
            Interaction.TOUCH_UP -> {
                resolveTransition()
            }
            Interaction.DRAGGED -> {
                Log.i(
                    "TAG",
                    "reactDrag: childView?.width:${childView?.width} leftImageView.width: ${leftImageView.width} childView?.x: ${childView?.x}"
                )
                childView!!.x = eventX - childRelativeXEvent
                setTrailingEffect()
            }
            Interaction.VOID -> Unit
            Interaction.SCROLLED -> Unit
        }
    }

    //TODO rename
    private fun setTrailingEffect() {
        leftImageView.visibility = View.VISIBLE
        leftImageView.layoutParams = FrameLayout.LayoutParams(
            childView!!.x.toInt(), childView!!.height
        )

    }

    private fun resolveTransition() {
        when {
            childView!!.x > this.width * 0.45 -> { // complete swipe to right and return
                acceleratedInterpolation(
                    from = childView!!.x, to = this.width, onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    }
                ) {
                    acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    })
                }
                /*childView?.accelerateOriginHorizontally(
                    to = this.width
                ) {
                    childView?.accelerateOriginHorizontally(to = 0)
                }*/
                transitTo(State.IDLE)
            }
            childView!!.x < -this.width * 0.45 -> { // complete swipe to left and return

                acceleratedInterpolation(
                    from = childView!!.x, to = -this.width, onValue = {
                        childView!!.x = it.toFloat()
                        //leftImageView.width(it)//TODO right
                    }
                ) {
                    acceleratedInterpolation(from = childView!!.x, to = 0, onValue = {
                        childView!!.x = it.toFloat()
                        //leftImageView.width(it) // TODO right
                    })
                }
                transitTo(State.IDLE)
            }
            childView!!.x > this.width * buttonWidthRatio -> { // shows left
                acceleratedInterpolation(from = childView!!.x, to = this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        leftImageView.width(it)
                    }
                )
                transitTo(State.HOLDING_LEFT)
            }
            childView!!.x < -this.width * buttonWidthRatio -> { // shows right
                acceleratedInterpolation(from = childView!!.x, to = -this.width * buttonWidthRatio,
                    onValue = {
                        childView!!.x = it.toFloat()
                        //leftImageView.width(it)
                    }
                )
                /*childView?.accelerateOriginHorizontally(
                    to = -this.width * buttonWidthRatio
                )*/
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