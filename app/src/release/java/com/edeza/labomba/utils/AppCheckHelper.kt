package com.edeza.labomba.utils

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

object AppCheckHelper {
    fun init() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }
}