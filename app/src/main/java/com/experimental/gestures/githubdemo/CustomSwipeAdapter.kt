package com.experimental.gestures.githubdemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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


/*class CustomSwipeAdapter {
}*/
class CustomSwipeAdapter(context: Context, objects: ArrayList<String>) :
    RecyclerSwipeAdapter<CustomSwipeAdapter.SimpleViewHolder?>() {
    class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swipeLayout: SwipeLayout
        //var textViewPos: TextView
        //var textViewData: TextView
        //var buttonDelete: Button

        init {
            swipeLayout = itemView.findViewById<View>(R.id.swipe) as SwipeLayout
            //textViewPos = itemView.findViewById<View>(R.id.position) as TextView
            //textViewData = itemView.findViewById<View>(R.id.text_data) as TextView
            //buttonDelete = itemView.findViewById<View>(R.id.delete) as Button
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //Log.d(javaClass.simpleName, "onItemSelected: " + textViewData.text.toString())
                    Toast.makeText(
                        view.context,
                        "onItemSelected: $adapterPosition",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private val mContext: Context
    private val mDataset: ArrayList<String>

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);
    init {
        mContext = context
        mDataset = objects
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_custom_github_swipe, parent, false)
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SimpleViewHolder, position: Int) {
        val item = mDataset[position]
        //viewHolder.swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        viewHolder.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
        val starBottView: View = viewHolder.swipeLayout.findViewById(R.id.starbott)
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper))
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper_2))
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Top, starBottView)
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Bottom, starBottView)
        viewHolder.swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                Toast.makeText(mContext, "ON_SIDE_CLICK", Toast.LENGTH_SHORT).show()
            }
        })
        viewHolder.swipeLayout.setOnDoubleClickListener { layout, surface ->
            Toast.makeText(
                mContext,
                "DoubleClick",
                Toast.LENGTH_SHORT
            ).show()
        }
        /*viewHolder.buttonDelete.setOnClickListener { view ->
            mItemManger.removeShownLayouts(viewHolder.swipeLayout)
            mDataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mDataset.size)
            mItemManger.closeAllItems()
            Toast.makeText(
                view.context,
                "Deleted ", //+ viewHolder.textViewData.text.toString() + "!",
                Toast.LENGTH_SHORT
            ).show()
        }*/
        //viewHolder.textViewPos.text = (position + 1).toString() + "."
        //viewHolder.textViewData.text = item
        mItemManger.bind(viewHolder.itemView, position)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }
}
