package com.example.gymworkoutapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.database.entities.HistoryWeight
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryWeightAdapter(
    private val items: List<HistoryWeight>
) : RecyclerView.Adapter<HistoryWeightAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textWeight: TextView = itemView.findViewById(R.id.history_weight_weight)
        val textDate: TextView = itemView.findViewById(R.id.history_weight_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_weight, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val weight = "${item.weight} kg"
        holder.textWeight.text = weight

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.textDate.text = formatter.format(item.datetime)
    }

    override fun getItemCount(): Int = items.size

}