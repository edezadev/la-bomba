package com.edeza.labomba

import android.app.Application
import com.edeza.labomba.utils.AppCheckHelper
import com.google.firebase.FirebaseApp

class LaBombaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppCheckHelper.init()
    }
}