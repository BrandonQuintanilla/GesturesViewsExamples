package com.experimental.gestures.component

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
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

    private var initialXEvent = 0f

    private var onActiveListener: OnActiveListener? = null

    @SuppressLint("ClickableViewAccessibility")
    private val buttonTouchListener: OnTouchListener = OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> childView?.isTouchInside(event) ?: false
            MotionEvent.ACTION_MOVE -> {

                Log.i("TAG", "onTouch event.x: " + event.x)
                Log.i("TAG", "onTouch childView!!.width: " + childView!!.width)

                if (initialXEvent == 0f) {
                    initialXEvent = event.x
                }

                //attach to center
                //swipeButtonInner!!.x = event.x - swipeButtonInner!!.width / 2

                //attach to touch point
                childView!!.x = event.x - initialXEvent

                true
            }
            MotionEvent.ACTION_UP -> {

                if (childView!!.x + childView!!.width > width * 0.9) {
                    onActiveListener?.onActive()
                }
                returnToOriginalPosition()

                true
            }
            else -> false
        }
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

    private fun returnToOriginalPosition() {
        Log.i("TAG", "onTouch childView!!.width: " + childView!!.width)

        val positionAnimator = ValueAnimator.ofFloat(childView!!.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            childView!!.x = x
            Log.i("TAG", "onTouch childView!!.width: " + childView!!.width)
        }
        positionAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(positionAnimator)
        animatorSet.start()
        initialXEvent = 0f
    }
}

fun View.isTouchInside(event: MotionEvent): Boolean {
    val isBeforeLeft = event.x <= this.x + this.width
    val isAfterRight = event.x > this.x
    return isBeforeLeft && isAfterRight
}