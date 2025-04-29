package com.example.gymworkoutapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.listeners.OnExerciseSelectedListener

class ExerciseDialogFragment(
    private val repository: ExerciseRepository,
    private val listener: OnExerciseSelectedListener
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = FrameLayout(requireContext())
        view.id = View.generateViewId()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(view.id, ExerciseFragment(repository, true, listener))
            .commit()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}