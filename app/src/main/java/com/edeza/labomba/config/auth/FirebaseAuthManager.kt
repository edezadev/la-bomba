package com.edeza.labomba.config.auth

import com.edeza.labomba.config.database.PenaltyDbManager
import com.edeza.labomba.utils.Logger
import com.google.firebase.auth.FirebaseAuth
import kotlin.lazy

object FirebaseAuthManager {
//  Inicializar Firebase Auth
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var uid: String

    fun createUserAnonymously(onResult: (Boolean) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result?.user
                    if (user != null) {
                        Logger.debug("CreatedUser", "Successfully created an anonymous user in Firebase")
//                        Crear datos inciales en la base de datos
                        PenaltyDbManager.saveDataUser(user.uid)
//                        Avisar éxito (Devuelve true) a la activity
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } else {
                    Logger.error("ErrorCreatingUser", "Error creating an anonymous user in Firebase", it.exception)
//                    Avisar error (devuelve false) a la activity
                    onResult(false)
                }
            }
    }

    fun getUid(callBack: (String) -> Unit) {
        if (FirebaseAuthManager::uid.isInitialized) {
            callBack(uid)
        } else {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                uid = currentUser.uid
                callBack(uid)
            }else {
                Logger.error("ErrorGettingUid", "Couldn't get UID, user not found")
            }
        }
    }
}