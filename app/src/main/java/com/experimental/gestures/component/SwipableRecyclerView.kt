package com.experimental.gestures.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


/**
 * Created by Brandon Quintanilla on Feb/27/2023
 */
class SwipableRecyclerView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : RecyclerView(ctx, attrs) {

    private var mIsDragging = false

    private var mLastX = 0f
    private var mLastY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->                 // reset drag state when touch down
                mIsDragging = false
            MotionEvent.ACTION_MOVE -> {
                // detect drag gesture
                val deltaX: Float = ev.x - mLastX
                mLastX =ev.x
                val deltaY: Float = ev.y - mLastY
                mLastY =ev.y
                if (abs(deltaX) > abs(deltaY)) {
                    mIsDragging = true
                    // return false to allow child views to handle touch event
                    return true
                }
            }
        }
        // always return super.onInterceptTouchEvent() unless we're dragging
        //return mIsDragging || super.onInterceptTouchEvent(ev)
        Log.i("SwipableRecyclerView", "onInterceptTouchEvent: $mIsDragging")
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val rr = super.onTouchEvent(ev)
        Log.i("SwipableRecyclerView", "onInterceptTouchEvent.onTouchEvent: $rr")
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN ->                 // reset drag state when touch down
                mIsDragging = false
            MotionEvent.ACTION_MOVE -> {
                // detect drag gesture
                val deltaX: Float = ev.x - mLastX
                mLastX =ev.x
                val deltaY: Float = ev.y - mLastY
                mLastY =ev.y
                if (abs(deltaX) > abs(deltaY)) {//is horizontal movemente
                    mIsDragging = true
                    // return false to allow child views to handle touch event
                    Log.i("SwipableRecyclerView", "onInterceptTouchEvent.onTouchEvent: false")
                    return true
                }
            }
        }
        return rr
    }


}