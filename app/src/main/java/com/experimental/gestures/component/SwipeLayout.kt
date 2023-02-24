package com.experimental.gestures.component

import android.animation.*
import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.experimental.gestures.R

/**
 * Created by Brandon Quintanilla on Feb/24/2023
 */
class SwipeLayout @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private var swipeButtonInner: ImageView? = null
    private var initialX = 0f
    var isActive = false
        private set
    private var centerText: TextView? = null
    private var background: ViewGroup? = null
    private var disabledDrawable: Drawable? = null
    private var enabledDrawable: Drawable? = null
    private var onActiveListener: OnActiveListener? = null
    private var collapsedWidth = 0
    private var collapsedHeight = 0
    private var layer: LinearLayout? = null
    private var trailEnabled = false
    private var hasActivationState = false
    private var buttonLeftPadding = 0f
    private var buttonTopPadding = 0f
    private var buttonRightPadding = 0f
    private var buttonBottomPadding = 0f


    init {
        setup(ctx, attrs)
    }

    override fun setBackground(drawable: Drawable) {
        background!!.background = drawable
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        swipeButtonInner = children.find { it is ImageView } as? ImageView
    }

    private fun setup(
        context: Context,
        attrs: AttributeSet?
    ) {
        hasActivationState = true
        background = RelativeLayout(context)
        val layoutParamsView = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        addView(background, layoutParamsView)
        val centerText = TextView(context)
        this.centerText = centerText
        centerText.gravity = Gravity.CENTER
        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        background!!.addView(centerText, layoutParams)
        //val swipeButton = ImageView(context)
        //swipeButtonInner = swipeButton

        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SwipeLayout
        )
        collapsedWidth = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_button_image_width,
            ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
        ).toInt()
        collapsedHeight = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_button_image_height,
            ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
        ).toInt()
        trailEnabled = typedArray.getBoolean(
            R.styleable.SwipeLayout_lay_button_trail_enabled,
            false
        )
        val trailingDrawable =
            typedArray.getDrawable(R.styleable.SwipeLayout_lay_button_trail_drawable)
        val backgroundDrawable =
            typedArray.getDrawable(R.styleable.SwipeLayout_lay_inner_text_background)
        if (backgroundDrawable != null) {
            background!!.setBackground(backgroundDrawable)
        } else {
            background!!.setBackground(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.shape_rounded
                )
            )
        }
        if (trailEnabled) {
            layer = LinearLayout(context)
            if (trailingDrawable != null) {
                layer!!.background = trailingDrawable
            } else {
                layer!!.background =
                    typedArray.getDrawable(R.styleable.SwipeLayout_lay_button_background)
            }
            layer!!.gravity = Gravity.START
            layer!!.visibility = View.GONE
            background!!.addView(layer, layoutParamsView)
        }
        centerText.text = typedArray.getText(R.styleable.SwipeLayout_lay_inner_text)
        centerText.setTextColor(
            typedArray.getColor(
                R.styleable.SwipeLayout_lay_inner_text_color,
                Color.WHITE
            )
        )
        val textSize = convertPixelsToSp(
            typedArray.getDimension(R.styleable.SwipeLayout_lay_inner_text_size, 0f), context
        )
        if (textSize != 0f) {
            centerText.textSize = textSize
        } else {
            centerText.textSize = 12f
        }
        disabledDrawable = typedArray.getDrawable(R.styleable.SwipeLayout_lay_button_image_disabled)
        enabledDrawable = typedArray.getDrawable(R.styleable.SwipeLayout_lay_button_image_enabled)
        val innerTextLeftPadding = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_inner_text_left_padding, 0f
        )
        val innerTextTopPadding = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_inner_text_top_padding, 0f
        )
        val innerTextRightPadding = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_inner_text_right_padding, 0f
        )
        val innerTextBottomPadding = typedArray.getDimension(
            R.styleable.SwipeLayout_lay_inner_text_bottom_padding, 0f
        )
        val initialState = typedArray.getInt(
            R.styleable.SwipeLayout_lay_initial_state,
            DISABLED
        )
        if (initialState == ENABLED) {
            val layoutParamsButton = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            //swipeButtonInner!!.setImageDrawable(enabledDrawable)
            //addView(swipeButtonInner, layoutParamsButton)
            isActive = true
        } else {
            val layoutParamsButton =
                RelativeLayout.LayoutParams(collapsedWidth, collapsedHeight)
            layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            //swipeButtonInner!!.setImageDrawable(disabledDrawable)
            //addView(swipeButtonInner, layoutParamsButton)
            isActive = false
        }
        centerText.setPadding(
            innerTextLeftPadding.toInt(),
            innerTextTopPadding.toInt(),
            innerTextRightPadding.toInt(),
            innerTextBottomPadding.toInt()
        )
        val buttonBackground =
            typedArray.getDrawable(R.styleable.SwipeButton_button_background)
        if (buttonBackground != null) {
            //swipeButtonInner!!.background = buttonBackground
        } else {
            //swipeButtonInner!!.background = ContextCompat.getDrawable(context, R.drawable.shape_button)
        }
        buttonLeftPadding =
            typedArray.getDimension(R.styleable.SwipeButton_button_left_padding, 0f)
        buttonTopPadding =
            typedArray.getDimension(R.styleable.SwipeButton_button_top_padding, 0f)
        buttonRightPadding =
            typedArray.getDimension(R.styleable.SwipeButton_button_right_padding, 0f)
        buttonBottomPadding =
            typedArray.getDimension(R.styleable.SwipeButton_button_bottom_padding, 0f)
        /*swipeButtonInner!!.setPadding(
            buttonLeftPadding.toInt(),
            buttonTopPadding.toInt(),
            buttonRightPadding.toInt(),
            buttonBottomPadding.toInt()
        )*/
        hasActivationState =
            typedArray.getBoolean(R.styleable.SwipeButton_has_activate_state, true)
        typedArray.recycle()

        setOnTouchListener(buttonTouchListener)
    }

    private val buttonTouchListener: OnTouchListener
        get() = object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> return !isTouchOutsideInitialPosition(
                        event,
                        swipeButtonInner!!
                    )
                    MotionEvent.ACTION_MOVE -> {

                        Log.i(
                            "SWIPE_BUTTON",
                            "onTouch: swipeButtonInner!!.x ${swipeButtonInner!!.x} width:$width  swipeButtonInner!!.width ${swipeButtonInner!!.width}"
                        )
                        if (initialX == 0f) {
                            initialX = swipeButtonInner!!.x
                        }
                        if (event.x > swipeButtonInner!!.width / 2 &&
                            event.x + swipeButtonInner!!.width / 2 < width
                        ) {
                            swipeButtonInner!!.x = event.x - swipeButtonInner!!.width / 2
                            centerText!!.alpha =
                                1 - 1.3f * (swipeButtonInner!!.x + swipeButtonInner!!.width) / width
                            setTrailingEffect()
                        }
                        if (event.x + swipeButtonInner!!.width / 2 > width &&
                            swipeButtonInner!!.x + swipeButtonInner!!.width / 2 < width
                        ) {
                            swipeButtonInner!!.x = width - swipeButtonInner!!.width.toFloat()
                        }
                        if (event.x < swipeButtonInner!!.width / 2) {
                            swipeButtonInner!!.x = 0f
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isActive) {
                            collapseButton()
                        } else {
                            if (swipeButtonInner!!.x + swipeButtonInner!!.width > width * 0.9) {
                                if (hasActivationState) {
                                    expandButton()
                                } else if (onActiveListener != null) {
                                    onActiveListener!!.onActive()
                                    moveButtonBack()
                                }
                            } else {
                                moveButtonBack()
                            }
                        }
                        return true
                    }
                }
                return false
            }
        }

    private fun expandButton() {
        val positionAnimator = ValueAnimator.ofFloat(swipeButtonInner!!.x, 0f)
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            swipeButtonInner!!.x = x
        }
        val widthAnimator = ValueAnimator.ofInt(
            swipeButtonInner!!.width,
            width
        )
        widthAnimator.addUpdateListener {
            val params = swipeButtonInner!!.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            swipeButtonInner!!.layoutParams = params
        }
        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isActive = true
                swipeButtonInner!!.setImageDrawable(enabledDrawable)

                if (onActiveListener != null) {
                    onActiveListener!!.onActive()
                }
            }
        })
        animatorSet.playTogether(positionAnimator, widthAnimator)
        animatorSet.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun moveButtonBack() {
        val positionAnimator = ValueAnimator.ofFloat(swipeButtonInner!!.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            swipeButtonInner!!.x = x
            setTrailingEffect()
        }
        positionAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (layer != null) {
                    layer!!.visibility = View.GONE
                }
            }
        })
        val objectAnimator = ObjectAnimator.ofFloat(centerText, "alpha", 1f)
        positionAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimator, positionAnimator)
        animatorSet.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun collapseButton() {
        val finalWidth: Int
        finalWidth = if (collapsedWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            swipeButtonInner!!.height
        } else {
            collapsedWidth
        }
        val widthAnimator =
            ValueAnimator.ofInt(swipeButtonInner!!.width, finalWidth)
        widthAnimator.addUpdateListener {
            val params = swipeButtonInner!!.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            swipeButtonInner!!.layoutParams = params
            setTrailingEffect()
        }
        widthAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isActive = false
                swipeButtonInner!!.setImageDrawable(disabledDrawable)

                if (layer != null) {
                    layer!!.visibility = View.GONE
                }
            }
        })
        val objectAnimator = ObjectAnimator.ofFloat(centerText, "alpha", 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimator, widthAnimator)
        animatorSet.start()
    }

    private fun setTrailingEffect() {
        if (trailEnabled) {
            layer!!.visibility = View.VISIBLE
            layer!!.layoutParams = RelativeLayout.LayoutParams(
                (swipeButtonInner!!.x + swipeButtonInner!!.width / 3).toInt(),
                centerText!!.height
            )
        }
    }

    companion object {
        private const val ENABLED = 0
        private const val DISABLED = 1
    }
}