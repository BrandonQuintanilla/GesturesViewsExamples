package com.experimental.gestures.component

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
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

    private val buttonTouchListener: OnTouchListener
        get() = object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> return !isTouchOutsideInitialPosition(
                        event, childView!!
                    )
                    MotionEvent.ACTION_MOVE -> {

                        Log.i("TAG", "onTouch event.x: " + event.x)

                        if (initialXEvent == 0f) {
                            initialXEvent = event.x
                        }

                        //attach to center
                        //swipeButtonInner!!.x = event.x - swipeButtonInner!!.width / 2

                        //attach to touch point
                        childView!!.x = event.x - initialXEvent

                        return true
                    }
                    MotionEvent.ACTION_UP -> {

                        if (childView!!.x + childView!!.width > width * 0.9) {
                            onActiveListener?.onActive()
                            moveButtonBack()
                        } else {
                            moveButtonBack()
                        }

                        return true
                    }
                }
                return false
            }
        }

    private fun moveButtonBack() {
        val positionAnimator = ValueAnimator.ofFloat(childView!!.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            childView!!.x = x
        }
        positionAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(positionAnimator)
        animatorSet.start()
        initialXEvent = 0f
    }


    companion object {
        private const val ENABLED = 0
        private const val DISABLED = 1
    }
}