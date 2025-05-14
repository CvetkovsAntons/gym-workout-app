package com.example.gymworkoutapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.WorkoutConfigActivity
import com.example.gymworkoutapp.activities.WorkoutPreviewActivity
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.example.gymworkoutapp.models.WorkoutData
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WorkoutListAdapter(
    private var items: MutableList<WorkoutData>,
    private val context: Context,
    private val lifecycleScope: CoroutineScope,
    private val repository: WorkoutRepository,
) : RecyclerView.Adapter<WorkoutListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.workout_name)
        val numberOfExercises: TextView = itemView.findViewById(R.id.workout_num_of_exercises)
        val icon: ShapeableImageView = itemView.findViewById(R.id.workout_icon)
        val iconMissing: TextView = itemView.findViewById(R.id.workout_icon_missing)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_icon)
        val updateButton: ImageView = itemView.findViewById(R.id.update_icon)
        val workout: ConstraintLayout = itemView.findViewById(R.id.workout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_workout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.showIcon(item)

        holder.name.text = item.name
        holder.numberOfExercises.text = item.exercises.size.toString()

        holder.updateButton.setOnClickListener {
            var workout = item.copy(image = null)
            workout.exercises.map { it.exercise.image = null }

            val intent = Intent(context, WorkoutConfigActivity::class.java)
            intent.putExtra("workout", workout)
            context.startActivity(intent)
        }
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Exercise")
                .setMessage("Are you sure you want to permanently delete exercise?")
                .setPositiveButton("Yes") { _, _ -> deleteExercise(item, position) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        holder.workout.setOnClickListener {
            val intent = Intent(context, WorkoutPreviewActivity::class.java)
            intent.putExtra("workout_id", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WorkoutData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    private fun ViewHolder.showIcon(item: WorkoutData) {
        if (item.image.isNullOrEmpty()) {
            showMissingIcon(item)
            return
        }

        try {
            val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            setIconAttributes(View.VISIBLE, bitmap)
            setIconMissingAttributes(View.GONE)
        } catch (e: Exception) {
            e.printStackTrace()
            showMissingIcon(item)
        }
    }

    private fun ViewHolder.showMissingIcon(item: WorkoutData) {
        setIconAttributes(View.GONE)
        setIconMissingAttributes(View.VISIBLE, item.name)
    }

    private fun ViewHolder.setIconAttributes(visibility: Int, bitmap: Bitmap? = null) {
        if (bitmap == null) {
            icon.setImageDrawable(null)
        } else {
            icon.setImageBitmap(bitmap)
        }
        icon.visibility = visibility
    }

    private fun ViewHolder.setIconMissingAttributes(visibility: Int, name: String? = null) {
        iconMissing.text = name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        iconMissing.visibility = visibility
    }

    private fun deleteExercise(exerciseData: WorkoutData, position: Int) {
        lifecycleScope.launch {
            repository.delete(exerciseData)

            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

}