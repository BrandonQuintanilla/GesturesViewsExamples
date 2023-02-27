package com.experimental.gestures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.experimental.gestures.databinding.ActivityMainBinding
import com.experimental.gestures.recycler.CustomSwipeAdapterconstructor
import com.experimental.gestures.recycler.SwipeAdapter
import com.experimental.gestures.recycler.SwipeToDeleteCallback


class MainActivity : AppCompatActivity() {

    private val bind: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        //setupRecycler()
        setupCustomSwipeAdapter()
        setupSwipe()
    }

    private fun setupCustomSwipeAdapter() {
        val swipeAdapter = CustomSwipeAdapterconstructor()
        bind.rvMain.adapter = swipeAdapter
        //bind.rvMain.onDragEvent()
        bind.rvMain
        //bind.rvMain.onInterceptTouchEvent()
        //bind.rvMain.requestDisallowInterceptTouchEvent(false)
    }

    private fun setupRecycler() {
        val swipeAdapter = SwipeAdapter()
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(swipeAdapter))
        itemTouchHelper.attachToRecyclerView(bind.rvMain)
        bind.rvMain.adapter = swipeAdapter
    }

    private fun setupSwipe() = bind.apply {
        laySwipe.onSwipe { direction ->
            Log.i("TAG", "setupSwipe: onSwipe ${direction}")
            Toast.makeText(applicationContext, "onSwipe ${direction}", Toast.LENGTH_LONG).show()
        }
        laySwipe.onLeftClick {
            Log.i("TAG", "setupSwipe: onLeftClick")
            Toast.makeText(applicationContext, "onLeftClick", Toast.LENGTH_LONG).show()
        }
        laySwipe.onRightClick {
            Log.i("TAG", "setupSwipe: onRightClick")
            Toast.makeText(applicationContext, "onRightClick", Toast.LENGTH_LONG).show()
        }
        layTest.setOnClickListener {
            Log.i("TAG", "setupSwipe: onChildClick")
            Toast.makeText(applicationContext, "onChildClick", Toast.LENGTH_LONG).show()
        }
    }
}