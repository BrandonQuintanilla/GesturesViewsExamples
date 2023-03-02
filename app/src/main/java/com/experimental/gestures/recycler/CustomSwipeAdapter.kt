package com.experimental.gestures.recycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.experimental.gestures.databinding.CustomItemSwipBinding

/**
 * Created by Brandon Quintanilla on Feb/27/2023
 */
class CustomSwipeAdapterconstructor constructor(val ctx: Context) :
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
                Toast.makeText(ctx, "onLeftClick", Toast.LENGTH_SHORT).show()
            }
            bind.root.onRightClick {
                Toast.makeText(ctx, "onRightClick", Toast.LENGTH_SHORT).show()
            }
            bind.root.onSwipe {
                Toast.makeText(ctx, "onSwipe", Toast.LENGTH_SHORT).show()
            }

            bind.tvMain.setOnClickListener {
                Toast.makeText(ctx, "tvMain click", Toast.LENGTH_SHORT).show()
            }
        }
    }
}