package com.experimental.gestures

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.experimental.gestures.databinding.ItemSwipeBinding

/**
 * Created by Brandon Quintanilla on Feb/22/2023
 */
class SwipeAdapter() : RecyclerView.Adapter<SwipeAdapter.ViewHolder>() {

    val data = listOf("Item1", "Item2", "Item3", "Item4", "Item5", "Item6")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = ItemSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun deleteItem(position: Int) {
        //TODO
    }

    inner class ViewHolder(private val bind: ItemSwipeBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(s: String) {
            bind.tvMain.text = s
        }
    }
}