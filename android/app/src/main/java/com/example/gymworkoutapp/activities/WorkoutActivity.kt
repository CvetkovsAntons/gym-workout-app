package com.example.gymworkoutapp.activities

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.data.mappers.toHistory
import com.example.gymworkoutapp.data.mappers.toWorkoutData
import com.example.gymworkoutapp.enums.WorkoutStatus
import com.example.gymworkoutapp.models.WorkoutExerciseSetData
import kotlinx.coroutines.launch
import java.sql.Timestamp

class WorkoutActivity : WorkoutPreviewActivity() {

    private var started = Timestamp(System.currentTimeMillis())
    private var historyWorkoutId: Int? = null
    private var totalVolume: Float = 0f
    override var isViewMode = false


    override suspend fun prepareWorkout() {
        val workoutId = intent.getIntExtra("workout_id", 0)

        val history = workoutRepository.getHistoryWorkout(workoutId, WorkoutStatus.IN_PROGRESS)

        if (history == null) {
            workout = workoutRepository.get(workoutId)
            return
        }

        historyWorkoutId = history.id
        totalVolume = history.totalVolume
        workout = history.toWorkoutData()
        started = history.startedAt
    }

    override fun showTimer(set: WorkoutExerciseSetData) {
        lifecycleScope.launch {
            val w = workout

            if (w == null) {
                return@launch
            }

            totalVolume += set.weight

            val history = workoutRepository.upsertHistoryWorkout(w.toHistory(
                historyWorkoutId,
                started,
                totalVolume
            ))
            if (history != null) {
                historyWorkoutId = history.id
            }

            super.showTimer(set)
        }
    }

    override fun finishWorkout() {
        lifecycleScope.launch {
            val w = workout

            if (w == null) {
                return@launch
            }

            workoutRepository.upsertHistoryWorkout(w.toHistory(
                historyWorkoutId,
                started,
                totalVolume,
                WorkoutStatus.FINISHED,
                Timestamp(System.currentTimeMillis())
            ))

            val intent = Intent(this@WorkoutActivity, WorkoutResultActivity::class.java)
            intent.putExtra("history_workout_id", historyWorkoutId)
            intent.putExtra("after_workout", true)
            startActivity(intent)

//            val intent = Intent(this@WorkoutActivity, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)

            finish()
        }
    }

}