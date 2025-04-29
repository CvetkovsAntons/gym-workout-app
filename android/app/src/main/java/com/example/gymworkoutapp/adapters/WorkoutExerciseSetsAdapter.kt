package com.example.gymworkoutapp.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
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

        holder.reps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toIntOrNull() ?: 0
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    items[holder.adapterPosition].reps = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.weight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toFloatOrNull() ?: 0f
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    items[holder.adapterPosition].weight = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.deleteIcon.setOnClickListener {
            val adapterPos = holder.adapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                items.removeAt(adapterPos)
                notifyItemRemoved(adapterPos)

                items.forEachIndexed { index, workoutExerciseSetData ->
                    workoutExerciseSetData.orderNum = index
                }

                notifyItemRangeChanged(0, items.size)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WorkoutExerciseSetData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

}