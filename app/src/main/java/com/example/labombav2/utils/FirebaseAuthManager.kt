package com.example.labombav2.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager {
//  Inicializar Firebase Auth
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var authToken: String

    fun createUserAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("CreatedUser", "Successfully created an anonymous user in Firebase")
                } else {
                    Log.e("ErrorCreatingUser", it.exception.toString())
                }
            }
    }

    fun getAuthToken(callback: (String) -> Unit) {
        if (::authToken.isInitialized) {
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
}