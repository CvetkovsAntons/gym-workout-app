package com.example.gymworkoutapp.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.enums.Filter
import com.google.android.material.button.MaterialButton

class ExerciseFragment(
    private var repository: ExerciseRepository
) : Fragment() {

    private lateinit var buttonFilterYou: MaterialButton
    private lateinit var buttonFilterOthers: MaterialButton
    private lateinit var buttonFilterDownloaded: MaterialButton

    private var selectedFilters = mutableListOf<Filter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonFilterYou = view.findViewById(R.id.exercises_btn_created_by_you)
        buttonFilterOthers = view.findViewById(R.id.exercises_btn_created_by_others)
        buttonFilterDownloaded = view.findViewById(R.id.exercises_btn_downloaded)

        prepareFilters()

        buttonFilterYou.setOnClickListener {
            changeFilter(Filter.CREATED_BY_YOU)
        }
        buttonFilterOthers.setOnClickListener {
            changeFilter(Filter.CREATED_BY_OTHERS)
        }
        buttonFilterDownloaded.setOnClickListener {
            changeFilter(Filter.DOWNLOADED)
        }
    }

    override fun onResume() {
        super.onResume()
        prepareFilters()
    }

    private fun changeFilter(filter: Filter) {
        if (selectedFilters.contains(filter)) {
            selectedFilters.remove(filter)
        } else {
            selectedFilters.add(filter)
        }
        setFilterColor(filter)
    }

    private fun setFilterColor(filter: Filter) {
        val button = when (filter) {
            Filter.CREATED_BY_YOU -> buttonFilterYou
            Filter.CREATED_BY_OTHERS -> buttonFilterOthers
            Filter.DOWNLOADED -> buttonFilterDownloaded
        }

        val selectedColor = ContextCompat.getColor(requireContext(), R.color.primary_darker)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.primary_lighter)

        button.backgroundTintList = ColorStateList.valueOf(
            if (filter in selectedFilters) selectedColor else unselectedColor
        )
    }

    private fun prepareFilters() {
        setFilterColor(Filter.CREATED_BY_YOU)
        setFilterColor(Filter.CREATED_BY_OTHERS)
        setFilterColor(Filter.DOWNLOADED)
    }
//
//    private fun getWorkout() {
//        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("workouts")
//            .addValueEventListener(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    usersWorkoutsList.clear()
//                    if (snapshot.exists()) {
//                        for (workoutSnap in snapshot.children) {
//                            val workoutData = workoutSnap.getValue(UsersWorkouts::class.java)
//                            workoutId = workoutSnap.key.toString()
//                            usersWorkoutsList.add(workoutData!!)
//                            idList.add(workoutId)
//                        }
//                        adapter()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//
//        database.getReference("users").child(auth.currentUser!!.uid).child("created")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    visibility()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
//    }
//
//    private fun  createWorkout() {
//        val snapshot = database.getReference("usersWorkouts").child(auth.currentUser!!.uid)
//        var id = 0
//
//        snapshot.child("created").get().addOnSuccessListener {
//            id = it.value.toString().toInt()
//            id += 1
//        }.addOnFailureListener {
//                Log.v("error", "error")
//            }
//
//        snapshot.get().addOnSuccessListener {
//
//            snapshot.child("workouts").child("usersWorkouts_$id").child("name").setValue("WORKOUT $id")
//            snapshot.child("workouts").child("usersWorkouts_$id").child("id").setValue("usersWorkouts_$id")
//            snapshot.child("workouts").child("usersWorkouts_$id").child("exercises").setValue(0)
//            snapshot.child("created").setValue(id)
//            val intent = Intent(this.context, CreateWorkoutActivity::class.java)
//            intent.putExtra("workout", "usersWorkouts_$id")
//            startActivity(intent)
//        }
//            .addOnFailureListener {
//                snapshot.child("workouts").child("usersWorkouts_1").child("name").setValue("WORKOUT $id")
//                snapshot.child("workouts").child("usersWorkouts_1").child("id").setValue("usersWorkouts_1")
//                snapshot.child("workouts").child("usersWorkouts_1").child("exercises").setValue(0)
//                snapshot.child("created").setValue(1)
//                val intent = Intent(this.context, CreateWorkoutActivity::class.java)
//                intent.putExtra("workout", "usersWorkouts_1")
//                startActivity(intent)
//            }
//
//        database.getReference("users").child(auth.currentUser!!.uid).child("created").setValue(count + 1)
//    }
//
//    private fun visibility() {
//        database.getReference("users").child(auth.currentUser!!.uid)
//            .get().addOnSuccessListener {
//                val c = it.child("created").value.toString()
//                count = c.toLong()
//                if (c.toLong() < 1.toLong()) {
//                    users_workouts_list.visibility = View.GONE
//                    users_workouts_delete.visibility = View.GONE
//                    no_workouts_created.visibility = View.VISIBLE
//                } else {
//                    users_workouts_list.visibility = View.VISIBLE
//                    users_workouts_delete.visibility = View.VISIBLE
//                    no_workouts_created.visibility = View.GONE
//                }
//            }.addOnFailureListener {
//                Log.v("error", "no workout")
//            }
//    }
//
//    private fun alertDialog() {
//        builder.setTitle("Are You Sure?")
//            .setMessage("Are you sure you  want to delete all your workouts?")
//            .setCancelable(true)
//            .setPositiveButton("Yes",
//                DialogInterface.OnClickListener {
//                        dialog, id -> deleteWorkouts()
//                })
//            .setNegativeButton("No",
//                DialogInterface.OnClickListener {
//                        dialog, id -> dialog.cancel()
//                })
//            .show()
//    }
//
//    private fun deleteWorkouts() {
//        database.getReference("users").child(auth.currentUser!!.uid).child("created").setValue(0)
//        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("workouts").removeValue()
//        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("created").setValue(0)
//        database.getReference("workoutExercises").child(auth.currentUser!!.uid).child("usersWorkouts").removeValue()
//    }
//
//    private fun adapter() {
//        if (activity != null) {
//            val adapter = UsersWorkoutsAdapter(usersWorkoutsList, requireActivity(), WorkoutsFragment())
//            recyclerView.adapter = adapter
//            adapter.setOnItemClickListener(object: UsersWorkoutsAdapter.onItemClickListener{
//                override fun onItemClick(position: Int) {
//                    val intent = Intent(activity, WorkoutPreviewActivity::class.java)
//                    intent.putExtra("id", idList[position])
//                    intent.putExtra("key", "usersWorkouts")
//                    startActivity(intent)
//                }
//            })
//        }
//    }
}