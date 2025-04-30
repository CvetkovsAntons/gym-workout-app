package com.example.gymworkoutapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.ExerciseActivity
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData
import com.example.gymworkoutapp.utils.base64ToBitmap
import com.google.android.material.imageview.ShapeableImageView

class WorkoutExerciseAdapter(
    private var items: MutableList<WorkoutExerciseData>,
    private val context: Context,
    private val onSetLogged: () -> Unit,
    private val showTimer: (WorkoutExerciseSetData) -> Unit,
    private val isViewMode: Boolean = false,
) : RecyclerView.Adapter<WorkoutExerciseAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val muscles: TextView = itemView.findViewById(R.id.exercise_muscles)
        val equipment: TextView = itemView.findViewById(R.id.exercise_equipment)
        val difficulty: TextView = itemView.findViewById(R.id.exercise_difficulty)
        val icon: ShapeableImageView = itemView.findViewById(R.id.exercise_icon)
        val iconMissing: TextView = itemView.findViewById(R.id.exercise_icon_missing)
        val deleteButton: ImageView = view.findViewById(R.id.delete_icon)
        val setList: RecyclerView = view.findViewById(R.id.set_list)
        val addButton: Button = view.findViewById(R.id.add_set)
        val exerciseInfo: LinearLayout = view.findViewById(R.id.exercise_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_workout_exercise, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val exercise = item.exercise

        if (!exercise.image.isNullOrEmpty()) {
            showIcon(holder, exercise)
        } else {
            showMissingIcon(holder, exercise)
        }

        holder.name.text = exercise.name
        holder.difficulty.text = exercise.difficulty.toString().lowercase()
        holder.muscles.text = displayListWithLimit(exercise.muscles, { it.name })
        holder.equipment.text = displayListWithLimit(exercise.equipment, { it.name })

        val setsAdapter = WorkoutExerciseSetsAdapter(item.sets, context, onSetLogged, showTimer, isViewMode)
        holder.setList.adapter = setsAdapter
        holder.setList.layoutManager = LinearLayoutManager(holder.itemView.context)

        holder.exerciseInfo.setOnClickListener {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra("exercise", exercise.copy(image = null))
            intent.putExtra("mode", ExerciseActivity.VIEW_MODE)
            context.startActivity(intent)
        }

        holder.addButton.setOnClickListener {
            item.sets.add(WorkoutExerciseSetData(item.sets.count(), 0, 0.toFloat()))
            setsAdapter.notifyItemInserted(item.sets.size - 1)
        }

        holder.deleteButton.setOnClickListener {
            items.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WorkoutExerciseData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    private fun showIcon(holder: ViewHolder, item: ExerciseData) {
        try {
            holder.icon.setImageBitmap(item.image.base64ToBitmap())
            holder.icon.visibility = View.VISIBLE
            holder.iconMissing.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            showMissingIcon(holder, item)
        }
    }

    private fun showMissingIcon(holder: ViewHolder, item: ExerciseData) {
        holder.icon.setImageDrawable(null)
        val firstLetter = item.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        holder.icon.visibility = View.GONE
        holder.iconMissing.text = firstLetter
        holder.iconMissing.visibility = View.VISIBLE
    }

    private fun <T> displayListWithLimit(list: List<T>, name: (T) -> String, limit: Int = 3): String {
        val names = list.map { name(it).lowercase() }

        return when {
            names.size <= limit -> names.joinToString(", ")
            else -> {
                val shown = names.take(limit - 1).joinToString(", ")
                "$shown and ${names.size - (limit - 1)} more"
            }
        }
    }

}