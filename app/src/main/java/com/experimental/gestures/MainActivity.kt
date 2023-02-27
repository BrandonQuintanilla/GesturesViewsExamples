package com.experimental.gestures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.experimental.gestures.databinding.ActivityMainBinding
import com.experimental.gestures.recycler.SwipeAdapter
import com.experimental.gestures.recycler.SwipeToDeleteCallback


class MainActivity : AppCompatActivity() {

    private val bind: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        setupRecycler()
        setupSwipe()
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
        }
        laySwipe.onLeftClick {
            Log.i("TAG", "setupSwipe: onLeftClick")
        }
        laySwipe.onRightClick {
            Log.i("TAG", "setupSwipe: onRightClick")
        }
        layTest.setOnClickListener {
            Log.i("TAG", "setupSwipe: onChildClick")
        }
    }
}