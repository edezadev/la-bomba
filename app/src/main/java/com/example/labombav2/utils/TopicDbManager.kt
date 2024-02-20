package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.TopicModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object TopicDbManager {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userRef by lazy { db.collection(Constants.USERS) }

    fun createTopic(uid: String, topic: TopicModel) {
        userRef.document(uid).collection(Constants.TOPICS).add(topic)
            .addOnSuccessListener {
                updateTopic(uid, it.id, mapOf(Constants.ID to it.id))
                Log.d("TopicSuccessfullyAdded", "New topic added successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorCreatingTopic", "Error adding a new topic", it)
            }
    }

    fun updateTopic(uid: String, id: String, updates: Map<String, Any>) {
        userRef.document(uid).collection(Constants.TOPICS).document(id).update(updates)
            .addOnSuccessListener {
                Log.d("TopicSuccessfullyUpdated", "The Topic was updated successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorUpdatingTopic", "Couldn't update the topic", it)
            }
    }

//  Método para obtener una página de datos de Firestore
    fun getTopicsPage(uid: String, numberPage: Int,
    onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        userRef.document(uid).collection(Constants.TOPICS)
            .orderBy(Constants.NAME)
            .limit(Constants.PAGE_SIZE.toLong())//Limitar el número de elementos por página
            .startAfter(numberPage * Constants.PAGE_SIZE)//Comenzar después del último elemento de la página anterior
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}