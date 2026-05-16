package com.edeza.labomba.config.auth

import android.util.Log
import com.edeza.labomba.config.database.PenaltyDbManager
import com.google.firebase.auth.FirebaseAuth
import kotlin.lazy

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

    fun createUserAnonymously(onResult: (Boolean) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result?.user
                    if (user != null) {
                        Log.d("CreatedUser", "Successfully created an anonymous user in Firebase")
//                        Crear datos inciales en la base de datos
                        PenaltyDbManager.saveDataUser(user.uid)
//                        Avisar éxito (Devuelve true) a la activity
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } else {
                    Log.e("ErrorCreatingUser", it.exception.toString())
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
                Log.e("ErrorGettingUid", "Couldn't get UID, user not found")
            }
        }
    }
}