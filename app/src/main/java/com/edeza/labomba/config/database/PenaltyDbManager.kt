package com.edeza.labomba.config.database

import android.annotation.SuppressLint
import com.edeza.labomba.models.PenaltyModel
import com.edeza.labomba.utils.Constants
import com.edeza.labomba.utils.Logger
import com.google.firebase.firestore.ListenerRegistration

object PenaltyDbManager {
    /*La anotación indica que somos conscientes de la referencia estática,
    pero es segura ya que Firebase usa el ApplicationContext internamente.*/
    @SuppressLint("StaticFieldLeak")
    private val db = FirestoreConfig.db
    private val userRef by lazy { db.collection(Constants.USERS) }

//   Crear el usuario con los temas predeterminados
    fun saveDataUser(uid: String) {
        val listPenalties: List<PenaltyModel> = listOf(
            PenaltyModel(name = "Bailar"),
            PenaltyModel(name = "Cantar"),
            PenaltyModel(name = "Beber"),
            PenaltyModel(name = "Contar un chiste"),
        )
        val penaltiesRef = userRef.document(uid).collection(Constants.PENALTIES)

        listPenalties.forEach{ penalty ->
            penaltiesRef.add(penalty)
                .addOnSuccessListener {
                    updatePenalty(uid, it.id, mapOf(Constants.ID to it.id))
                    Logger.debug("DataSuccessfullyAdded", "Penalty created successfully")
                }
                .addOnFailureListener {
                    Logger.error("ErrorAddingData", "Error creating penalty", it)
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
                    Logger.error("ErrorGettingPenalties",
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
                Logger.debug("PenaltySuccessfullyAdded", "New penalty added successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorCreatingPenalty", "Error adding a new penalty", it)
            }

    }

     fun updatePenalty(uid: String, id: String, updates: Map<String, Any>){
        userRef.document(uid).collection(Constants.PENALTIES).document(id).update(updates)
            .addOnSuccessListener {
                Logger.debug("PenaltySuccessfullyUpdated", "The penalty was updated successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorUpdatingPenalty", "Couldn't update the penalty", it)
            }
    }

    fun deletePenalty(uid: String, idPenalty: String){
        userRef.document(uid).collection(Constants.PENALTIES).document(idPenalty).delete()
            .addOnSuccessListener {
                Logger.debug("PenaltyDeleted", "The penalty was deleted successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorDeletingPenalty", "Couldn't delete the penalty", it)
            }
    }
}