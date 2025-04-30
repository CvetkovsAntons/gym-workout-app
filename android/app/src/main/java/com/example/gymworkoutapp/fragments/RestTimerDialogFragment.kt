package com.example.gymworkoutapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.gymworkoutapp.R

class RestTimerDialogFragment : DialogFragment() {

    private var remainingTime = 90_000L // 1 minute 30 seconds
    private var countDownTimer: CountDownTimer? = null

    private lateinit var timerText: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.workout_rest_timer, null, false)

        timerText = view.findViewById(R.id.timer_text)
        val addButton: Button = view.findViewById(R.id.add_time_btn)
        val subtractButton: Button = view.findViewById(R.id.subtract_time_btn)

        updateTimerText()
        startTimer()

        addButton.setOnClickListener {
            remainingTime += 5_000
            restartTimer()
        }

        subtractButton.setOnClickListener {
            remainingTime = maxOf(5_000, remainingTime - 5_000)
            restartTimer()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                dismiss()
            }
        }.start()
    }

    private fun restartTimer() {
        countDownTimer?.cancel()
        startTimer()
    }

    private fun updateTimerText() {
        val seconds = (remainingTime / 1000) % 60
        val minutes = (remainingTime / 1000) / 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
