package com.experimental.gestures.github.demo_recycler

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.experimental.gestures.R
import com.experimental.gestures.github.library.Attributes
import java.util.*

class RecyclerViewExample : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    //private val mAdapter: RecyclerView.Adapter? = null
    private var mAdapter: RecyclerViewAdapter? = null

    private var mDataSet: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_example)

        ////////////////////
        // Layout Managers:

        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView

        // Layout Managers:
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        // Item Decorator:

        // Item Decorator:
        //recyclerView.addItemDecoration(DividerItemDecoration(resources.getDrawable(R.drawable.divider)))
        //recyclerView.itemAnimator = FadeInLeftAnimator()

        // Adapter:

        // Adapter:
        val adapterData = arrayOf(
            "Alabama",
            "Alaska",
            "Arizona",
            "Arkansas",
            "California",
            "Colorado",
            "Connecticut",
            "Delaware",
            "Florida",
            "Georgia",
            "Hawaii",
            "Idaho",
            "Illinois",
            "Indiana",
            "Iowa",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Maine",
            "Maryland",
            "Massachusetts",
            "Michigan",
            "Minnesota",
            "Mississippi",
            "Missouri",
            "Montana",
            "Nebraska",
            "Nevada",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "New York",
            "North Carolina",
            "North Dakota",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Vermont",
            "Virginia",
            "Washington",
            "West Virginia",
            "Wisconsin",
            "Wyoming"
        )
        mDataSet = java.util.ArrayList(Arrays.asList(*adapterData))
        mAdapter = RecyclerViewAdapter(this, mDataSet)
        (mAdapter as RecyclerViewAdapter).mode = Attributes.Mode.Single
        recyclerView!!.adapter = mAdapter

        /* Listeners */

        /* Listeners */recyclerView!!.setOnScrollListener(onScrollListener)
    }

    var onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.e("ListView", "onScrollStateChanged")
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // Could hide open views here if you wanted. //
        }
    }
}