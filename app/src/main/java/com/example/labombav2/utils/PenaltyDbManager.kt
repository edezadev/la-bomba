package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.PenaltyModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object PenaltyDbManager {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userRef by lazy { db.collection(Constants.USERS) }

//   Crear el usuario con los temas predeterminados
    fun saveDataUser(uid: String) {
        val listPenalties: List<PenaltyModel> = listOf(
            PenaltyModel(name = "Bailar", isChecked = false),
            PenaltyModel(name = "Cantar", isChecked = false),
            PenaltyModel(name = "Beber", isChecked = false),
            PenaltyModel(name = "Contar un chiste", isChecked = false),
        )
        val penaltiesRef = userRef.document(uid).collection(Constants.PENALTIES)

        listPenalties.forEach{ penalty ->
            penaltiesRef.add(penalty)
                .addOnSuccessListener {
                    updatePenalty(uid, it.id, mapOf(Constants.ID to it.id))
                    Log.d("DataSuccessfullyAdded", "Penalty created successfully")
                }
                .addOnFailureListener {
                    Log.e("ErrorAddingData", "Error creating penalty", it)

                }
        }
    }

    /* Obtener la lsita de los castigos del usuario cada vez que haya un cambio en la colección de
     * la BD y retornar el Listener Registration para manejar la escucha de cambios y evitar fugas
     * de memoria */
    fun getPenaltiesListener(uid: String, listenerPenalties: (MutableList<PenaltyModel>) -> Unit): ListenerRegistration {
        return userRef.document(uid).collection(Constants.PENALTIES).orderBy("name")
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
                //  Actualizar el id que por defecto que se crea vacío
                updatePenalty(uid, it.id, mapOf(Constants.ID to it.id))
                Log.d("PenaltySuccessfullyAdded", "New penalty added successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorCreatingPenalty", "Error adding a new penalty", it)
            }

    }

     fun updatePenalty(uid: String, id: String, updates: Map<String, Any>){
        userRef.document(uid).collection(Constants.PENALTIES).document(id).update(updates)
            .addOnSuccessListener {
                Log.d("PenaltySuccessfullyUpdated", "The penalty was updated successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorUpdatingPenalty", "Couldn't update the penalty", it)
            }
    }

    fun deletePenalty(uid: String, idPenalty: String){
        userRef.document(uid).collection(Constants.PENALTIES).document(idPenalty).delete()
            .addOnSuccessListener {
                Log.d("PenaltyDeleted", "The penalty was deleted successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorDeletingPenalty", "Couldn't delete the penalty",it)
            }
    }
}