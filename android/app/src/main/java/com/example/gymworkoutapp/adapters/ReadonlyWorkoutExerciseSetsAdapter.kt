package com.example.gymworkoutapp.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.models.WorkoutExerciseSetData

class ReadonlyWorkoutExerciseSetsAdapter(
    private var items: MutableList<WorkoutExerciseSetData>,
    private val context: Context
) : RecyclerView.Adapter<ReadonlyWorkoutExerciseSetsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val setOrderNum: TextView = view.findViewById(R.id.workout_exercises_sets_set)
        val reps: EditText = view.findViewById(R.id.workout_exercises_sets_reps)
        val weight: EditText = view.findViewById(R.id.workout_exercises_sets_weight)
        val deleteIcon: ImageView = view.findViewById(R.id.workout_exercises_delete)
        val checkedIcon: ImageView = view.findViewById(R.id.workout_checked)
        val uncheckedIcon: ImageView = view.findViewById(R.id.workout_unchecked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_sets, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val set = items[position]

        holder.setData(set)

        holder.deleteIcon.visibility = View.GONE
        holder.checkedIcon.visibility = View.GONE
        holder.uncheckedIcon.visibility = View.GONE
        holder.reps.isEnabled = false
        holder.weight.isEnabled = false
    }

    override fun getItemCount(): Int = items.size

    private fun ViewHolder.setData(set: WorkoutExerciseSetData) {
        setOrderNum.setText("SET " + (adapterPosition + 1))
        reps.setText(set.reps.toString())
        weight.setText(set.weight.toString())
    }

    private fun ViewHolder.checkedIconState(set: WorkoutExerciseSetData) {
        val button = if (set.isLogged) checkedIcon else uncheckedIcon

        button.setOnClickListener {
            if (!set.isLogged && reps.text.toString().toInt() < 1) {
                Toast.makeText(context, "Can't log step with 0 reps", Toast.LENGTH_SHORT).show()
            } else {
                set.isLogged = !set.isLogged
                notifyItemChanged(adapterPosition)
            }
        }
    }

}