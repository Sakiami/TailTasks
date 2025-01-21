package com.example.tailtasks

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tailtasks.models.health_model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Health : AppCompatActivity() {
    //DECLARATION

    //database
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    //TextView
    private lateinit var lastVaccination : TextView

    //EditText
    private lateinit var temperature : EditText
    private lateinit var weight : EditText
    private lateinit var foodIntake : EditText

    //Buttons
    private lateinit var saveRecord : Button
    private lateinit var cancelButton : Button
    private lateinit var datePickerBtn : ImageButton

    //Spinner
    private lateinit var energySpinner : Spinner
    private lateinit var intakeSpinner : Spinner
    private lateinit var exercisefreqSpinner : Spinner

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)

        //INITIALIZATION

        //database
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Owners")

        //TextView
        lastVaccination = findViewById(R.id.lastVaccination)

        //EditText
        temperature = findViewById(R.id.tempBox)
        weight = findViewById(R.id.weightBox)
        foodIntake = findViewById(R.id.foodBox)

        //Buttons
        saveRecord = findViewById(R.id.saveRecord)
        cancelButton = findViewById(R.id.cancelButton2)
        datePickerBtn = findViewById(R.id.datePicker)

        //Spinner
        energySpinner = findViewById(R.id.energySpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.energy,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            energySpinner.adapter = adapter
        }

        intakeSpinner = findViewById(R.id.foodSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.frequency,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            intakeSpinner.adapter = adapter
        }

        exercisefreqSpinner = findViewById(R.id.exerSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.exercise_frequency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            exercisefreqSpinner.adapter = adapter
        }

        //Shared Preferences
        sharedPreferences = getSharedPreferences("Session_Details", MODE_PRIVATE)
        val ownerID = sharedPreferences.getString("OwnerID","")
        val petID = sharedPreferences.getString("PetID","")

        //Calendar
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLabel(myCalendar)
        }

        //Button press events
        datePickerBtn.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        lastVaccination.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        saveRecord.setOnClickListener {
            saveHealthRecord(ownerID,petID)
        }

        cancelButton.setOnClickListener {
            //TO DO: dialog for confirmation
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveHealthRecord(ownerID:String?,petID:String?) {
        //getting values of each edit boxes
        var temp = temperature.text.toString().toFloat()
        var weight = weight.text.toString().toFloat()

        var energyLevel = energySpinner.selectedItem.toString()
        var foodIntake = foodIntake.text.toString()+" "+intakeSpinner.selectedItem.toString()
        var exerciseFreq = exercisefreqSpinner.selectedItem.toString()

        var lastVax = lastVaccination.getText().toString()
        val current = Date()
        val currentDate = ""+current.getMonth()+"-"+current.getDate()+"-"+current.getYear()

        val healthID = "HEALTH_"+currentDate

        //create health model
        var health = health_model(healthID, temp, weight,energyLevel,foodIntake, exerciseFreq, lastVax,currentDate)

        dbRef.child(ownerID!!).child("Pets").child(petID!!).child("HealthRecords").setValue(health).addOnSuccessListener {
            //if successful, toast
            Toast.makeText(this, "Added a new record", Toast.LENGTH_LONG).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        lastVaccination.setText(sdf.format(myCalendar.time))
    }
}