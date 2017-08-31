package com.dokufitness.finnvonholten.doku_fitness

import com.google.firebase.database.DatabaseReference

class Uploader {
    fun upLoadFitnessElement(databaseReference: DatabaseReference, userId: String, fitnessElement: FitnessElement) {
        val timestamp = System.currentTimeMillis().toString()
        databaseReference.child("Users").child(userId).child(timestamp).child("Duration").setValue(fitnessElement.duration)
        databaseReference.child("Users").child(userId).child(timestamp).child("Fitness-Tool").setValue(fitnessElement.tool)
        databaseReference.child("Users").child(userId).child(timestamp).child("Rating").setValue(fitnessElement.rating)
        databaseReference.child("Users").child(userId).child(timestamp).child("Reps").setValue(fitnessElement.reps)
        databaseReference.child("Users").child(userId).child(timestamp).child("Sets").setValue(fitnessElement.sets)
    }
}

