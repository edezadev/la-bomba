package com.example.labombav2.utils

import android.util.Log
import com.example.labombav2.model.TopicModel
import com.google.firebase.firestore.FirebaseFirestore

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

    private fun updateTopic(uid: String, id: String, updates: Map<String, Any>) {
        userRef.document(uid).collection(Constants.TOPICS).document(id).update(updates)
            .addOnSuccessListener {
                Log.d("TopicSuccessfullyUpdated", "The Topic was updated successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorUpdatingTopic", "Couldn't update the topic", it)
            }
    }
}