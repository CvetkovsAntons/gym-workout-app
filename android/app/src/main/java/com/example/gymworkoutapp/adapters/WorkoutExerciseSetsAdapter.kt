package com.example.gymworkoutapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData

class WorkoutExerciseSetsAdapter(
    private var items: MutableList<WorkoutExerciseSetData>
) : RecyclerView.Adapter<WorkoutExerciseSetsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reps: EditText = view.findViewById(R.id.workout_exercises_sets_reps)
        val weight: EditText = view.findViewById(R.id.workout_exercises_sets_weight)
        val deleteIcon: ImageView = view.findViewById(R.id.workout_exercises_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_sets, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val set = items[position]

        holder.reps.setText(set.reps.toString())
        holder.weight.setText(set.weight.toString())

        if (getItemCount() > 1) {
            holder.deleteIcon.visibility = View.VISIBLE
            holder.deleteIcon.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
            }
        } else {
            holder.deleteIcon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WorkoutExerciseSetData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

}