package com.example.tailtasks.models

//Made to create the pet model array list that will be inserted in the Pet node in the database. Must always be associated with an existing User
data class pet_model(var PetID:String?=null,
                     var PetName:String?=null,
                     var OwnerID:String?=null,
//                     var OwnerName:String?=null,
                     var Species:String?=null,
                     var Breed:String?=null,
                     var Gender:String?=null,
                     var Age:String?=null)
