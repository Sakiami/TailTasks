package com.example.tailtasks

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.tailtasks.models.owner_model
import com.example.tailtasks.models.pet_model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    //Declaration
    private lateinit var registerLink : TextView
    private lateinit var loginBtn : Button
    private lateinit var usernameBox : EditText
    private lateinit var passwordBox : EditText

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialization
        registerLink = findViewById(R.id.registerLink)
        loginBtn = findViewById(R.id.loginBtn)
        usernameBox = findViewById(R.id.usernameBox)
        passwordBox = findViewById(R.id.passwordBox)

        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Owners")

        registerLink.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            login(usernameBox.text.toString(),passwordBox.text.toString())
        }
    }

    private fun login(username: String, password: String) {
        dbRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(userSnapshot in dataSnapshot.children){
                        val userData = userSnapshot.getValue((owner_model::class.java))
                        if(userData!=null && userData.Password==password){
                            Toast.makeText(this@Login,"Welcome, "+userData.Username+"!",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login, MainActivity::class.java)
                            val sharedPreferences = getSharedPreferences("Session_Details", MODE_PRIVATE)
                            val myEdit = sharedPreferences.edit()

                            myEdit.putString("OwnerID",userData.OwnerID)
                            myEdit.putString("Username",userData.Username)
                            myEdit.putString("OwnerName",userData.OwnerName)

                            dbRef.child(userData.OwnerID!!).child("Pets").addValueEventListener(object :
                                ValueEventListener {
                                    override fun onDataChange(petDataSnapshot: DataSnapshot) {
                                        if(petDataSnapshot.exists()) {
                                            for (petSnapshot in petDataSnapshot.children) {
                                                val petData = petSnapshot.getValue((pet_model::class.java))

                                                myEdit.putString("PetID",petData!!.PetID)
                                                myEdit.putString("PetName",petData!!.PetName)
                                                myEdit.putString("PetBreed",petData!!.Breed)

                                                myEdit.apply()
                                            }
                                        }
                                    }


                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@Login,"Failed to retrieve data",Toast.LENGTH_SHORT).show()
                                    }
                                })

                            startActivity(intent)
                            finish()
                            return
                        }
                        else if(userData!=null && userData.Password!=password){
                            Toast.makeText(this@Login,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Toast.makeText(this@Login,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Login,"Database Error: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("MissingSuperCall")
    @Override
    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Cannot go back!", Toast.LENGTH_SHORT).show()
    }


}