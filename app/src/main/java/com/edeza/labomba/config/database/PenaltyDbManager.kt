package com.edeza.labomba.config.database

import android.annotation.SuppressLint
import com.edeza.labomba.models.PenaltyModel
import com.edeza.labomba.utils.Constants
import com.edeza.labomba.utils.Logger
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

object PenaltyDbManager {
    /*La anotación indica que somos conscientes de la referencia estática,
    pero es segura ya que Firebase usa el ApplicationContext internamente.*/
    @SuppressLint("StaticFieldLeak")
    private val db = FirestoreConfig.db
    private val userRef by lazy { db.collection(Constants.USERS) }

//   Crear el usuario con los temas predeterminados
    fun saveDataUser(uid: String, onResult: (Boolean) -> Unit) {
        val listPenalties: List<PenaltyModel> = listOf(
            PenaltyModel(name = "Hacer 15 sentadillas"),
            PenaltyModel(name = "Cambiar tu foto de perfil por 5 minutos"),
            PenaltyModel(name = "Beber un shot"),
            PenaltyModel(name = "Hablar como bebé"),
        )
        val penaltiesRef = userRef.document(uid).collection(Constants.PENALTIES)
        val tasks = listPenalties.map { penalty -> penaltiesRef.add(penalty) }

    Tasks.whenAllSuccess<DocumentReference>(tasks)
        .addOnSuccessListener { results ->
            results.forEach { docRef -> updatePenalty(uid, docRef.id, mapOf(Constants.ID to docRef.id)) }
            Logger.debug("DataSuccessfullyAdded", "Penalty created successfully")
            onResult(true)
        }
        .addOnFailureListener {
            Logger.error("ErrorAddingData", "Error creating penalty", it)
            onResult(false)
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