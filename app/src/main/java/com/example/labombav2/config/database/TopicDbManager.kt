package com.example.labombav2.config.database

import android.annotation.SuppressLint
import android.util.Log
import com.example.labombav2.models.TopicModel
import com.example.labombav2.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.ceil

object TopicDbManager {
    @SuppressLint("StaticFieldLeak")
    private val db = FirestoreConfig.db
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

                if (snapshot == null) return@addSnapshotListener

//                Todoo lo que pase aquí no bloquea la interfaz del usuario
                CoroutineScope(Dispatchers.IO).launch {
    //              Calcular la cantidad total de páginas
                    val totalPages = ceil(
                        snapshot.documents.size.toDouble() / Constants.PAGE_SIZE.toDouble()
                    ).toInt()
                    val listPages = mutableListOf<MutableList<TopicModel>>()
                    var lastDocument: DocumentSnapshot? = null

//                    Carga de páginas asíncrona
                    repeat (totalPages) {
                        /*Evita "congelar" el hilo, la coroutine se "pausa" hasta que Firestore
                        * responda(desde la nube o la caché offline). Cuando recibe los datos,
                        * continúa a la siguiente página*/
                        val query = getPage(uid, lastDocument) //Llama a .get().await internamente
                        if (query != null) {
                            val page = query.documents.mapNotNull {
                                it.toObject(TopicModel::class.java) }.toMutableList()
//                            Guarda la página y actualiza el último documento
                            if (page.isNotEmpty()) {
                                listPages.add(page)
                                lastDocument = query.documents.lastOrNull()
                            }
                        }
                    }
//                Volvemos al hilo principal para avisar a la UI
                    launch ( Dispatchers.Main ) {
                        listenerTopics(listPages)
                    }
                }
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
            Log.e("ErrorTopicsPage", "Failed to load the topics page", e)
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
