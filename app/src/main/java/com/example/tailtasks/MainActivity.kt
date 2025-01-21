package com.example.tailtasks

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tailtasks.models.health_model
import com.example.tailtasks.models.task_model
import com.example.tailtasks.recycler.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    //DECLARATION

    //database
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    //TextViews
    private lateinit var petName : TextView
    private lateinit var petBreed : TextView
    private lateinit var userGreet : TextView
    private lateinit var exerciseFrequency : TextView
    private lateinit var temperature : TextView
    private lateinit var weight : TextView
    private lateinit var energy : TextView
    private lateinit var vaccine : TextView

    //Buttons
    private lateinit var addTask : ImageButton
    private lateinit var trackHealth : ImageButton

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    //ArrayList
    private var taskList:ArrayList<task_model> = ArrayList()

    //RecyclerView
    private lateinit var taskRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //INITIALIZATION
        //database
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Owners")

        //TextViews
        petName = findViewById(R.id.petNameTxt)
        petBreed = findViewById(R.id.petBreedTxt)
        userGreet = findViewById(R.id.userTxt)
        energy = findViewById(R.id.energy)
        weight = findViewById(R.id.weight)
        exerciseFrequency = findViewById(R.id.exerciseFreq)
        temperature = findViewById(R.id.temp)
        vaccine = findViewById(R.id.vaxx)

        //Buttons
        addTask = findViewById(R.id.taskBtn)
        trackHealth = findViewById(R.id.healthBtn)

        //SP
        sharedPreferences = getSharedPreferences("Session_Details", MODE_PRIVATE)

        //RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecycler)
        taskRecyclerView.setLayoutManager(LinearLayoutManager(this,RecyclerView.VERTICAL, false))

        //set petname and breed on main activity
        petName.setText(sharedPreferences.getString("PetName",""))
        petBreed.setText(sharedPreferences.getString("PetBreed",""))

        //get first name
        var parts  = sharedPreferences.getString("OwnerName","")!!.split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        userGreet.setText("Hello, "+firstName+"!")

        addTask.setOnClickListener {
            val intent = Intent(this,Task::class.java)
            startActivity(intent)
        }

        trackHealth.setOnClickListener {
            val intent = Intent(this,Health::class.java)
            startActivity(intent)
        }

        getTasks(sharedPreferences.getString("OwnerID",""),sharedPreferences.getString("PetID",""))
        getLatestHealthRecord(sharedPreferences.getString("OwnerID",""),sharedPreferences.getString("PetID",""))
    }

    private fun getLatestHealthRecord(OwnerID: String?, PetID: String?) {
        dbRef.child(OwnerID!!).child("Pets").child(PetID!!).child("HealthRecords").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
/*                    for(health in snapshot.children){
                        var healthModel = snapshot.getValue(health_model::class.java)
                        energy.setText("Energy: "+healthModel!!.EnergyLevel)
                        weight.setText("Weight: "+healthModel!!.Weight.toString()+" kg")
                        exerciseFrequency.setText("Exercise Frequency: "+healthModel!!.ExerciseFreq!!.toString())
                        temperature.setText("Temperature: "+healthModel!!.Temperature!!.toString())
                        vaccine.setText("Vaccine: "+healthModel!!.LastVaccination!!.toString())
                        Log.d("Health",""+health)
                    }*/
                    var healthModel = snapshot.getValue(health_model::class.java)
                    energy.setText("Energy: "+healthModel!!.EnergyLevel)
                    weight.setText("Weight: "+healthModel!!.Weight.toString()+" kg")
                    exerciseFrequency.setText("Exercise Frequency: "+healthModel!!.ExerciseFreq!!.toString())
                    temperature.setText("Temperature: "+healthModel!!.Temperature!!.toString())
                    vaccine.setText("Vaccine: "+healthModel!!.LastVaccination!!.toString())
                }
                else{

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getTasks(OwnerID: String?, PetID: String?) {
        dbRef.child(OwnerID!!).child("Pets").child(PetID!!).child("Tasks").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                    for(taskSnap in snapshot.children){
                            var taskModel = taskSnap.getValue(task_model::class.java)
                            taskList!!.add(taskModel!!)
                    }
                    val taskRecycler = TaskAdapter(taskList!!)
                    taskRecyclerView.adapter = taskRecycler
                }
                else{
                    val taskRecycler = TaskAdapter(taskList!!)
                    taskRecyclerView.adapter = taskRecycler
//                    taskEmpty.setVisibility(View.VISIBLE)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}