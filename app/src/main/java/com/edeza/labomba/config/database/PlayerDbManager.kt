package com.edeza.labomba.config.database

import android.annotation.SuppressLint
import com.edeza.labomba.models.PlayerModel
import com.edeza.labomba.utils.Constants
import com.edeza.labomba.utils.Logger
import com.google.firebase.firestore.ListenerRegistration

object PlayerDbManager {
    @SuppressLint("StaticFieldLeak")
    private val db = FirestoreConfig.db
    private val userRef by lazy { db.collection(Constants.USERS) }

    fun createPlayer(uid: String, player: PlayerModel) {
        userRef.document(uid).collection(Constants.PLAYERS).add(player)
            .addOnSuccessListener {
                updatePlayer(uid, it.id, mapOf(Constants.ID to it.id))
                Logger.debug("PlayerSuccessfullyAdded", "New player added successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorCreatingPlayer", "Error adding a new player", it)
            }
    }

    fun updatePlayer(uid: String, id: String, updates: Map<String, Any>) {
        userRef.document(uid).collection(Constants.PLAYERS).document(id).update(updates)
            .addOnSuccessListener {
                Logger.debug("PlayerSuccessfullyUpdated", "The player was updated successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorUpdatingPlayer", "Couldn't updated the player", it)
            }
    }

    fun getPlayersListener(uid: String, listenerPlayers: (MutableList<PlayerModel>) -> Unit) : ListenerRegistration {
        return userRef.document(uid).collection(Constants.PLAYERS).orderBy(Constants.NAME)
            .addSnapshotListener{snapshot, error ->
                if (error != null) {
                    Logger.error("ErrorGettingPlayers",
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
                Logger.debug("PlayerDeleted", "The player was deleted successfully")
            }
            .addOnFailureListener {
                Logger.error("ErrorDeletingPlayer", "Couldn't delete the player", it)
            }
    }
}