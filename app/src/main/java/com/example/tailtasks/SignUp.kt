package com.example.tailtasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.tailtasks.models.owner_model
import com.example.tailtasks.models.pet_model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    //Declaration
    private lateinit var registerBtn : Button
    private lateinit var cancelBtn : Button
    private lateinit var dbRef: DatabaseReference

    //for owner details
    private lateinit var ownerBox : EditText
    private lateinit var usernameBox : EditText
    private lateinit var passwordBox : EditText

    //for pet details
    private lateinit var petNameBox : EditText
    private lateinit var speciesBox : EditText
    private lateinit var breedBox : EditText
    private lateinit var sexSpinner : Spinner
    private lateinit var ageBox : EditText
    private lateinit var ageSpinner : Spinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //Initialization
        registerBtn = findViewById(R.id.registerBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        ownerBox = findViewById(R.id.ownerNameBox)
        usernameBox = findViewById(R.id.userNameBox)
        passwordBox = findViewById(R.id.passWordBox)

        petNameBox = findViewById(R.id.petNameBox)
        speciesBox = findViewById(R.id.speciesBox)
        breedBox = findViewById(R.id.breedBox)
        sexSpinner = findViewById(R.id.sexSpinner)
        ageBox = findViewById(R.id.ageBox)
        ageSpinner = findViewById(R.id.ageSpinner)

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.sex,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            sexSpinner.adapter = adapter
        }

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.age,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            ageSpinner.adapter = adapter
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Owners")

        cancelBtn.setOnClickListener {
            //TO DO: dialog for confirmation
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        //getting values of each edit boxes
        val ownerName = ownerBox.text.toString()
        val username =usernameBox.text.toString()
        val pwd =passwordBox.text.toString()

        val petName =petNameBox.text.toString()
        val species =speciesBox.text.toString()
        val breed =breedBox.text.toString()
        val sex = sexSpinner.selectedItem.toString()
        val age =ageBox.text.toString() + " " + ageSpinner.selectedItem.toString()

        val ownerID = "OWNER_"+dbRef.push().key!!
        val petID = "PET_"+dbRef.push().key!!
        val owner = owner_model(ownerID,ownerName,username,pwd)
        val pet = pet_model(petID,petName,ownerID,species,breed,sex,age)

        dbRef.child(ownerID).setValue(owner).addOnSuccessListener {
            //needs to be successful because pet cannot exist in the system without an owner
            //if successful, save pet data
            savePetData(ownerID,pet)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePetData(ownerID: String, pet: pet_model) {
        dbRef.child(ownerID).child("Pets").child(pet.PetID!!).setValue(pet).addOnSuccessListener {
            Toast.makeText(this, "Successfully registered", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
        }
    }
}