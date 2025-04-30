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

class WorkoutExerciseSetsAdapter(
    private var items: MutableList<WorkoutExerciseSetData>,
    private val context: Context,
    private val onSetLogged: () -> Unit
) : RecyclerView.Adapter<WorkoutExerciseSetsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.findViewById(R.id.set_container)
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
        holder.setTextChangeListeners()

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

    private fun ViewHolder.setData(set: WorkoutExerciseSetData) {
        setOrderNum.setText("SET " + (adapterPosition + 1))
        reps.setText(set.reps.toString())
        weight.setText(set.weight.toString())

        if (set.isLogged) {
            container.setBackgroundResource(R.drawable.rounded_container_lighter_logged)
            uncheckedIcon.visibility = View.GONE
            checkedIcon.visibility = View.VISIBLE
        } else {
            container.setBackgroundResource(R.drawable.rounded_container_lighter)
            uncheckedIcon.visibility = View.VISIBLE
            checkedIcon.visibility = View.GONE
        }

        checkedIconState(set)
    }

    private fun ViewHolder.checkedIconState(set: WorkoutExerciseSetData) {
        val button = if (set.isLogged) checkedIcon else uncheckedIcon

        button.setOnClickListener {
            if (!set.isLogged && reps.text.toString().toInt() < 1) {
                Toast.makeText(context, "Can't log step with 0 reps", Toast.LENGTH_SHORT).show()
            } else {
                set.isLogged = !set.isLogged
                notifyItemChanged(adapterPosition)
                onSetLogged()
            }
        }
    }

    private fun ViewHolder.setTextChangeListeners() {
        reps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toIntOrNull() ?: 0
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    items[adapterPosition].reps = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        weight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toFloatOrNull() ?: 0f
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    items[adapterPosition].weight = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}