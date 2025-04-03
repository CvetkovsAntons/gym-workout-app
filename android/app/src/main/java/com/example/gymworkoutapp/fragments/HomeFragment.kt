package com.example.gymworkoutapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
//    private lateinit var workoutArrayList : ArrayList<>
    private var idList = arrayListOf<String>()
    private var workoutId : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        recyclerView = (view.findViewById(R.id.premade_workouts)) as RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        workoutArrayList = arrayListOf<PremadeWorkouts>()
//
//        recyclerView.visibility = View.GONE
//
//        refDb.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                workoutArrayList.clear()
//                if (snapshot.exists()) {
//                    for (workoutSnap in snapshot.children) {
//                        val workoutData = workoutSnap.getValue(PremadeWorkouts::class.java)
//                        workoutId = workoutSnap.key
//                        workoutArrayList.add(workoutData!!)
//                        idList.add(workoutId!!)
//                    }
//                    val adapter = PremadeWorkoutAdapter(workoutArrayList)
//                    recyclerView.adapter = adapter
//                    adapter.setOnItemClickListener(object: PremadeWorkoutAdapter.onItemClickListener{
//                        override fun onItemClick(position: Int) {
//                            val intent = Intent(activity, WorkoutPreviewActivity::class.java)
//                            intent.putExtra("id", idList[position])
//                            intent.putExtra("key", "premadeWorkouts")
//                            startActivity(intent)
//                        }
//                    })
//                }
//                recyclerView.visibility = View.VISIBLE
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//    }
}