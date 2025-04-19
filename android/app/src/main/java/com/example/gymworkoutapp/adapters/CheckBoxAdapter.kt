package com.example.gymworkoutapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R

class CheckBoxAdapter<T>(
    private val items: List<T>,
    private val selectedItems: MutableList<T>,
    private val label: (T) -> String
) : RecyclerView.Adapter<CheckBoxAdapter<T>.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.item_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.checkbox.text = label(item)
        holder.checkbox.isChecked = selectedItems.contains(item)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedItems.contains(item)) selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

}