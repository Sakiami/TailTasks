package com.example.tailtasks.models

import java.util.Date

data class health_model(var HealthID:String?=null,
                        var Temperature:Float?=0f,
                        var Weight:Float?=0f,
                        var EnergyLevel:String?=null,
                        var FoodIntake:String?=null,
                        var ExerciseFreq:String?=null,
                        var LastVaccination: String?=null,
                        var DateCreated:String?=null)
