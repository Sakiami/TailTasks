package com.example.tailtasks

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.tailtasks.models.task_model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Task : AppCompatActivity() {
    //DECLARATION

    //database
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    //TextView
    private lateinit var dueDate : TextView

    //EditText
    private lateinit var taskName : EditText
    private lateinit var taskFreq : EditText
    private lateinit var taskDescription : EditText

    //Buttons
    private lateinit var createTask : Button
    private lateinit var cancelButton : Button
    private lateinit var datePickerBtn : ImageButton

    //Spinner
    private lateinit var freqSpinner : Spinner

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        //INITIALIZATION

        //database
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Owners")

        //TextView
        dueDate = findViewById(R.id.dueDateBox)

        //EditText
        taskName = findViewById(R.id.taskNameBox)
        taskFreq = findViewById(R.id.freqBox)
        taskDescription = findViewById(R.id.descriptionBox)

        //Buttons
        createTask = findViewById(R.id.createTaskButton)
        cancelButton = findViewById(R.id.cancelButton)
        datePickerBtn = findViewById(R.id.datePicker)

        //Spinner
        freqSpinner = findViewById(R.id.freqSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.frequency,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            freqSpinner.adapter = adapter
        }

        //Calendar
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLabel(myCalendar)
        }

        //Shared Preferences
        sharedPreferences = getSharedPreferences("Session_Details", MODE_PRIVATE)
        val ownerID = sharedPreferences.getString("OwnerID","")
        val petID = sharedPreferences.getString("PetID","")

        //Button press events
        cancelButton.setOnClickListener{
            //TO DO: dialog for confirmation
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        createTask.setOnClickListener{
            saveTask(ownerID!!,petID!!)
        }

        datePickerBtn.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dueDate.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dueDate.setText(sdf.format(myCalendar.time))
    }

    private fun saveTask(ownerID: String, petID: String) {
        //getting values of each edit boxes
        var taskName = taskName.text.toString()
        var taskFreq = taskFreq.text.toString() + " "+freqSpinner.selectedItem.toString()
        var taskDescription = taskDescription.text.toString()
        var dueDate = dueDate.getText().toString()

        val taskID = "TASK_"+dbRef.push().key!!
        var task = task_model(taskID,taskName,taskFreq,dueDate,taskDescription)
        dbRef.child(ownerID).child("Pets").child(petID).child("Tasks").child(taskID).setValue(task).addOnSuccessListener {
            //if successful, toast
            Toast.makeText(this, "Added a new task", Toast.LENGTH_LONG).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
        }
    }
}