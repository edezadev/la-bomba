package com.example.labombav2.config.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

object FirestoreConfig {
//    Lazy asegura que se inicialice solo la primera vez que alguien lo pida
    val db : FirebaseFirestore by lazy {
        val firestore = FirebaseFirestore.getInstance()
//        Configuramos la persistencia offline
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        firestore.firestoreSettings = settings
        firestore
    }
}