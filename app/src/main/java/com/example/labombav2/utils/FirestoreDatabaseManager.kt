package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.PenaltyModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object FirestoreDatabaseManager {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userRef by lazy { db.collection(Constants.USERS) }

//   Crear el usuario con los temas predeterminados
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
                    Log.d("DataSuccessfullyAdded", "Penalty created successfully")
                }
                .addOnFailureListener {
                    Log.e("ErrorAddingData", "Error creating penalty")

                }
        }
    }

    /* Obtener la lsita de los castigos del usuario cada vez que haya un cambio en la colección de
     * la BD y retornar el Listener Registration para manejar la escucha de cambios y evitar fugas
     * de memoria */
    fun getPenaltiesListener(uid: String, listenerPenalties: (MutableList<PenaltyModel>) -> Unit): ListenerRegistration {
        return userRef.document(uid).collection(Constants.PENALTIES)
            .addSnapshotListener{snapshot, error ->
                if (error != null) {
                    Log.e("ErrorGettingPenalties",
                        "The penalties for the user could not be retrieved", error)
                    return@addSnapshotListener
                }

                val penalties = mutableListOf<PenaltyModel>()

                snapshot?.documents?.forEach {
                    val penalty = it.toObject(PenaltyModel::class.java)
                    penalty?.let { model -> penalties.add(model) }
                }

                listenerPenalties(penalties)
            }

    }

//  Crar un nuevo castigo para el usuario
    fun createPenalty(uid: String, penalty: PenaltyModel) {
        userRef.document(uid).collection(Constants.PENALTIES).add(penalty)
            .addOnSuccessListener {
                Log.d("PenaltySuccessfullyAdded", "New penalty added successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorCreatingPenalty", "Error adding a new penalty", it)
            }

    }
}