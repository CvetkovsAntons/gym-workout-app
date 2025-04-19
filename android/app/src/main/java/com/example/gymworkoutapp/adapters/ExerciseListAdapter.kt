package com.example.gymworkoutapp.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.models.ExerciseData
import com.google.android.material.imageview.ShapeableImageView

class ExerciseListAdapter(
    private var items: MutableList<ExerciseData>
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val muscles: TextView = itemView.findViewById(R.id.exercise_muscles)
        val equipment: TextView = itemView.findViewById(R.id.exercise_equipment)
        val icon: ShapeableImageView = itemView.findViewById(R.id.exercise_icon)
        val iconMissing: TextView = itemView.findViewById(R.id.exercise_icon_missing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (!item.image.isNullOrEmpty()) {
            showIcon(holder, item)
        } else {
            showMissingIcon(holder, item)
        }

        holder.name.text = item.name
        holder.muscles.text = displayListWithLimit(item.muscles, { it.name })
        holder.equipment.text = displayListWithLimit(item.equipment, { it.name })
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ExerciseData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    private fun showIcon(holder: ViewHolder, item: ExerciseData) {
        try {
            val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.icon.setImageBitmap(bitmap)
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
        val names = list.map { name(it) }

        return when {
            names.size <= limit -> names.joinToString(", ")
            else -> {
                val shown = names.take(limit - 1).joinToString(", ")
                "$shown and ${names.size - (limit - 1)} more"
            }
        }
    }

}