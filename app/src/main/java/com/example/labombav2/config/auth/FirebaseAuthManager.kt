package com.example.labombav2.config.auth

import android.util.Log
import com.example.labombav2.config.database.PenaltyDbManager
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager {
//  Inicializar Firebase Auth
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var authToken: String
    private lateinit var uid: String

    fun getAuthToken(callback: (String) -> Unit) {
        if (FirebaseAuthManager::authToken.isInitialized) {
//          si ya está inicializado devuelve el token existente
            callback(authToken)
        } else {
//          Obtiene el token y lo almacena
            val currentUser = auth.currentUser
            currentUser?.getIdToken(true)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        authToken = it.result.token.toString()
                        Log.d("TokenFound", "Successfully acquired user")
                        callback(authToken)
                    } else {
                        Log.e("ErrorGettingToken", it.exception.toString())
                        callback("")
                    }
                }
        }
    }

    fun createUserAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("CreatedUser", "Successfully created an anonymous user in Firebase")
//                  crear documento del usuario en la base de datos
                    PenaltyDbManager.saveDataUser(auth.uid!!)
                } else {
                    Log.e("ErrorCreatingUser", it.exception.toString())
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
                Log.e("ErrorGettingUid", "Couldn't get UID, user not found")
            }
        }
    }
}