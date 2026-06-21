package com.edeza.labomba.utils

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

object AppCheckHelper {
    fun init() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }
}