package com.experimental.gestures

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.experimental.gestures.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val bind: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        setupRecycler()
    }

    private fun setupRecycler() {
        val swipeAdapter = SwipeAdapter()
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(swipeAdapter))
        itemTouchHelper.attachToRecyclerView(bind.rvMain)
        bind.rvMain.adapter = swipeAdapter
    }
}