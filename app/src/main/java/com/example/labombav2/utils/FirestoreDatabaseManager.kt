package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.PenaltyModel
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreDatabaseManager {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val listPenalties: List<PenaltyModel> = listOf(
        PenaltyModel("Bailar", false),
        PenaltyModel("Cantar", false),
        PenaltyModel("Beber", false),
        PenaltyModel("Contar un chiste", false),
    )

    val prueba = PenaltyModel("Bailar", false)
    fun saveDataUser(uid: String) {
        val collectionRef = db.collection("users").document(uid).collection("penalties")

        listPenalties.forEach{penalty ->
            collectionRef.add(penalty)
                .addOnSuccessListener {
                    Log.d("DataSuccesfullyAdded", "Penalty successfully created")
                }
                .addOnFailureListener {
                    Log.e("ErrorAddingData", "Error creating penalty")

                }
        }
    }

    fun createPenalty(penalty: PenaltyModel) {

    }
}