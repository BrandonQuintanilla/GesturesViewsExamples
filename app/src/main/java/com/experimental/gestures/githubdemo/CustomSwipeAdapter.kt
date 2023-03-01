package com.experimental.gestures.githubdemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.experimental.gestures.R
import com.experimental.gestures.github.library.RecyclerSwipeAdapter
import com.experimental.gestures.github.library.SimpleSwipeListener
import com.experimental.gestures.github.library.SwipeLayout

/**
 * Created by Brandon Quintanilla on Feb/28/2023
 */

class CustomSwipeAdapter(context: Context, objects: ArrayList<String>) :
    RecyclerSwipeAdapter<CustomSwipeAdapter.SimpleViewHolder?>() {
    class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swipeLayout: SwipeLayout
        var archive: View
        var delete: View
        var marginfier: View
        var star: View
        var trash: View
        var tvItem: TextView

        init {
            swipeLayout = itemView.findViewById<View>(R.id.swipe) as SwipeLayout
            archive = itemView.findViewById<View>(R.id.archive)
            delete = itemView.findViewById<View>(R.id.delete)
            marginfier = itemView.findViewById<View>(R.id.magnifier2)
            star = itemView.findViewById<View>(R.id.star2)
            trash = itemView.findViewById<View>(R.id.trash2)
            tvItem = itemView.findViewById<TextView>(R.id.tv_item)
            tvItem.setOnClickListener {
                Toast.makeText(
                    it.context,
                    "onItemSelected: $adapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }
            /*itemView.setOnClickListener { view ->
                Toast.makeText(
                    view.context,
                    "onItemSelected: $adapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        }
    }

    private val mContext: Context
    private val mDataset: ArrayList<String>

    init {
        mContext = context
        mDataset = objects
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_custom_github_swipe, parent, false)
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SimpleViewHolder, position: Int) {
        val item = mDataset[position]
        viewHolder.swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        val starBottView: View = viewHolder.swipeLayout.findViewById(R.id.starbott)
        viewHolder.swipeLayout.addDrag(
            SwipeLayout.DragEdge.Left,
            viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper)
        )
        viewHolder.swipeLayout.addDrag(
            SwipeLayout.DragEdge.Right,
            viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper_2)
        )
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Top, starBottView)
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Bottom, starBottView)
        viewHolder.swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout) {
                //Toast.makeText(mContext, "SWIPED", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "onOpen SWIPED: ")
            }
        })
        viewHolder.swipeLayout.setOnDoubleClickListener { layout, surface ->
            Toast.makeText(
                mContext,
                "DoubleClick",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewHolder.archive.setOnClickListener {
            mItemManger.closeAllItems()
            Toast.makeText(
                mContext,
                "Archive",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewHolder.delete.setOnClickListener {
            mItemManger.closeAllItems()
            Toast.makeText(
                mContext,
                "Delete",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewHolder.marginfier.setOnClickListener {
            mItemManger.closeAllItems()
            Toast.makeText(
                mContext,
                "Magnifier",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewHolder.star.setOnClickListener {
            mItemManger.closeAllItems()
            Toast.makeText(
                mContext,
                "Star",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewHolder.trash.setOnClickListener {
            mItemManger.closeAllItems()
            Toast.makeText(
                mContext,
                "Trash",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewHolder.tvItem.text = item
        /*viewHolder.tvItem.setOnClickListener {
            Toast.makeText(
                mContext,
                "tvItem",
                Toast.LENGTH_SHORT
            ).show()
        }*/

        mItemManger.bind(viewHolder.itemView, position)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }
}
