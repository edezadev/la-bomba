package com.edeza.labomba.config.database

import android.annotation.SuppressLint
import com.edeza.labomba.models.TopicModel
import com.edeza.labomba.utils.Constants
import com.edeza.labomba.utils.Logger
import com.google.firebase.firestore.ListenerRegistration

object TopicDbManager {
    @SuppressLint("StaticFieldLeak")
    private val db = FirestoreConfig.db
    private val userRef by lazy { db.collection(Constants.USERS) }

    fun createTopic(uid: String, topic: TopicModel) {
        userRef.document(uid).collection(Constants.TOPICS).add(topic)
            .addOnSuccessListener {
                updateTopic(uid, it.id, mapOf(Constants.ID to it.id))
                Logger.debug("TopicSuccessfullyAdded", "New topic added successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorCreatingTopic", "Error adding a new topic", it)
            }
    }

    fun updateTopic(uid: String, id: String, updates: Map<String, Any>) {
        userRef.document(uid).collection(Constants.TOPICS).document(id).update(updates)
            .addOnSuccessListener {
                Logger.debug("TopicSuccessfullyUpdated", "The Topic was updated successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorUpdatingTopic", "Couldn't update the topic", it)
            }
    }

    fun getListPagesListener(uid: String, listenerTopics: (MutableList<MutableList<TopicModel>>) -> Unit) : ListenerRegistration {
        return userRef.document(uid).collection(Constants.TOPICS)
            .orderBy(Constants.NAME)
            .addSnapshotListener {snapshot, error ->
                if (error != null) {
                    Logger.error("ErrorGettingTopics",
                        "The topics for user could not be retrieved", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                val allTopics = snapshot.documents.mapNotNull {
                    it.toObject(TopicModel::class.java)
                }

                /* chunked() divide la lista completa en sublistas de PAGE_SIZE elementos cada
                 una, paginando en memoria sin llamadas adicionales a Firestore*/
                val listPages = allTopics
                    .chunked(Constants.PAGE_SIZE)
                    .map { page -> page.toMutableList() }
                    .toMutableList()

                listenerTopics(listPages)
            }
    }

    fun getTopic (uid: String, idTopic: String, topic: (TopicModel) -> Unit) {
        userRef.document(uid).collection(Constants.TOPICS).document(idTopic).get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(TopicModel::class.java)?.let { topic(it) }
            }
    }

    fun deleteTopic(uid: String, idTopic: String) {
        userRef.document(uid).collection(Constants.TOPICS).document(idTopic).delete()
            .addOnSuccessListener {
                Logger.debug("TopicDeleted", "The topic was deleted successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorDeletingTopic", "Couldn't delete the topic", it)
            }
    }
}
