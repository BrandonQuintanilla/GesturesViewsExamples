package com.experimental.gestures.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.experimental.gestures.databinding.CustomItemSwipBinding

/**
 * Created by Brandon Quintanilla on Feb/27/2023
 */
class CustomSwipeAdapterconstructor() :
    RecyclerView.Adapter<CustomSwipeAdapterconstructor.ViewHolder>() {

    val data = listOf("Item1", "Item2", "Item3", "Item4", "Item5", "Item6")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = CustomItemSwipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun deleteItem(position: Int) {
        //TODO
    }

    inner class ViewHolder(private val bind: CustomItemSwipBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(s: String) {
            bind.tvMain.text = s
            bind.root.onLeftClick {
                Log.i("TAG", "bind: onLeftClick")
            }
            bind.root.onRightClick {
                Log.i("TAG", "bind: onRightClick")
            }
            bind.root.onSwipe {
                Log.i("TAG", "bind: onSwipe")
            }
        }
    }
}