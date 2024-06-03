package com.example.labombav2.config.database

import android.util.Log
import com.example.labombav2.models.PlayerModel
import com.example.labombav2.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object PlayerDbManager {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userRef by lazy { db.collection(Constants.USERS) }

    fun createPlayer(uid: String, player: PlayerModel) {
        userRef.document(uid).collection(Constants.PLAYERS).add(player)
            .addOnSuccessListener {
                updatePlayer(uid, it.id, mapOf(Constants.ID to it.id))
                Log.d("PlayerSuccessfullyAdded", "New player added successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorCreatingPlayer", "Error adding a new player", it)
            }
    }

    fun updatePlayer(uid: String, id: String, updates: Map<String, Any>) {
        userRef.document(uid).collection(Constants.PLAYERS).document(id).update(updates)
            .addOnSuccessListener {
                Log.d("PlayerSuccessfullyUpdated", "The player was updated successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorUpdatingPlayer", "Couldn't updated the player", it)
            }
    }

    fun getPlayersListener(uid: String, listenerPlayers: (MutableList<PlayerModel>) -> Unit) : ListenerRegistration {
        return userRef.document(uid).collection(Constants.PLAYERS).orderBy(Constants.NAME)
            .addSnapshotListener{snapshot, error ->
                if (error != null) {
                    Log.e("ErrorGettingPlayers",
                        "The players for the user could not be retrieved", error)
                    return@addSnapshotListener
                }

                val listPlayers = mutableListOf<PlayerModel>()
                snapshot?.documents?.forEach {
                    val player = it.toObject(PlayerModel::class.java)
                    player?.let { model -> listPlayers.add(model) }
                }

                listenerPlayers(listPlayers)
            }
    }

    fun getPlayer(uid: String, idPlayer: String, player: (PlayerModel) -> Unit)  {
        userRef.document(uid).collection(Constants.PLAYERS).document(idPlayer).get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(PlayerModel::class.java)?.let { player(it) }
            }
    }

    fun deletePlayer(uid: String, idPlayer: String) {
        userRef.document(uid).collection(Constants.PLAYERS).document(idPlayer).delete()
            .addOnSuccessListener {
                Log.d("PlayerDeleted", "The player was deleted successfully")
            }
            .addOnFailureListener {
                Log.e("ErrorDeletingPlayer", "Couldn't delete the player", it)
            }
    }
}