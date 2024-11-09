package com.example.labombav2.config.database

import android.util.Log
import com.example.labombav2.models.TopicModel
import com.example.labombav2.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.math.ceil

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

//  Métodoo para obtener una página de datos de Firestore
    fun getListPagesListener(uid: String, listenerTopics: (MutableList<MutableList<TopicModel>>) -> Unit) : ListenerRegistration {
        return userRef.document(uid).collection(Constants.TOPICS)
            .addSnapshotListener {snapshot, error ->
                if (error != null) {
                    Log.e("ErrorGettingTopics",
                        "The topics for user could not be retrieved", error)
                    return@addSnapshotListener
                }

//              Calcular la cantidad total de páginas
                var totalPages = 0
                if (snapshot != null){
                    totalPages = ceil(
                        snapshot.documents.size.toDouble() / Constants.PAGE_SIZE.toDouble()
                    ).toInt()
                }

                var lastDocument: DocumentSnapshot? = null
                var query: QuerySnapshot?
                val listPages = mutableListOf<MutableList<TopicModel>>()
                var page: MutableList<TopicModel>

                for (i in 1..totalPages) {
                    page = mutableListOf()
                    runBlocking {
                        query = getPage(uid, lastDocument)
                        if (query != null) {
                            lastDocument = query!!.documents.lastOrNull()
                            query!!.documents.map { document ->
                                document.toObject(TopicModel::class.java)?.let { doc -> page.add(doc) }
                            }
                            listPages.add(page)
                        }
                    }
                }
                listenerTopics(listPages)
            }
    }

    private suspend fun getPage(uid: String, last: DocumentSnapshot?): QuerySnapshot? {
        return try {
            val query = userRef.document(uid).collection(Constants.TOPICS)
                .orderBy(Constants.NAME)
                .limit(Constants.PAGE_SIZE.toLong())//Limitar el número de elementos por página
            val result = if (last != null){
                query.startAfter(last)
            }else  {
                query
            }

            result.get().await()
        }catch (e: Exception) {
            null
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
                Log.d("TopicDeleted", "The topic was deleted successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorDeletingTopic", "Couldn't delete the topic", it)
            }
    }
}
