package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.PenaltyModel
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreDatabaseManager {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val userRef by lazy { db.collection(Constants.USERS) }

    fun saveDataUser(uid: String) {
        val listPenalties: List<PenaltyModel> = listOf(
            PenaltyModel("Bailar", false),
            PenaltyModel("Cantar", false),
            PenaltyModel("Beber", false),
            PenaltyModel("Contar un chiste", false),
        )
        val penaltiesRef = userRef.document(uid).collection(Constants.PENALTIES)

        listPenalties.forEach{ penalty ->
            penaltiesRef.add(penalty)
                .addOnSuccessListener {
                    Log.d("DataSuccesfullyAdded", "Penalty successfully created")
                }
                .addOnFailureListener {
                    Log.e("ErrorAddingData", "Error creating penalty")

                }
        }
    }

    fun getPenalties(uid: String, callback: (PenaltyModel) -> Unit) {
        userRef.document(uid).collection(Constants.PENALTIES).get()
            .addOnSuccessListener {result ->
                for (document in result) {
                    callback(
                        PenaltyModel(
                            document.data["name"].toString(),
                            document.data["isChecked"].toString().toBoolean()
                        )
                    )
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "ERROR AL CONSEGUIR LOS CASTIGOS")
            }

    }

    fun createPenalty(penalty: PenaltyModel) {

    }
}