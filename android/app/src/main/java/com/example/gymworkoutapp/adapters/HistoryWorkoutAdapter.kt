package com.example.gymworkoutapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.WorkoutResultActivity
import com.example.gymworkoutapp.models.HistoryWorkoutData
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryWorkoutAdapter(
    private val items: List<HistoryWorkoutData>,
    private val context: Context
) : RecyclerView.Adapter<HistoryWorkoutAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.workout_name)
        val status: TextView = itemView.findViewById(R.id.workout_status)
        val startedAt: TextView = itemView.findViewById(R.id.workout_started_at)
        val finishedAt: TextView = itemView.findViewById(R.id.workout_finished_at)
        val totalVolume: TextView = itemView.findViewById(R.id.workout_total_volume)
        val viewIcon: ImageView = itemView.findViewById(R.id.view_icon)
        val container: ConstraintLayout = itemView.findViewById(R.id.workout_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_history_workout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.workout.name
        holder.status.text = item.status.name

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.startedAt.text = dateFormatter.format(item.startedAt)
        holder.finishedAt.text = if (item.finishedAt != null) {
            dateFormatter.format(item.finishedAt)
        } else {
            "-"
        }

        holder.totalVolume.text = "${item.totalVolume} KG"

        holder.buttonListeners(item)
    }

    override fun getItemCount(): Int = items.size

    private fun ViewHolder.buttonListeners(item: HistoryWorkoutData) {
        viewIcon.setOnClickListener {
            openResult(item)
        }
        container.setOnClickListener {
            openResult(item)
        }
    }

    private fun openResult(item: HistoryWorkoutData) {
        val intent = Intent(context, WorkoutResultActivity::class.java)
        intent.putExtra("history_workout_id", item.id)
        context.startActivity(intent)
    }

}