package com.example.gymworkoutapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R

class TextListAdapter<T>(
    private val items: MutableList<T>,
    private val getText: (T) -> String,
    private val setText: (T, String) -> Unit
) : RecyclerView.Adapter<TextListAdapter<T>.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.text_list_text)
        val removeButton: Button = itemView.findViewById(R.id.text_list_remove_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.editText.setText(getText(item))

        holder.editText.doAfterTextChanged {
            setText(item, it.toString())
        }

        holder.removeButton.visibility = if (position == 0) View.GONE else View.VISIBLE

        holder.removeButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                items.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemChanged(items.lastIndex)
            }
        }
    }

}