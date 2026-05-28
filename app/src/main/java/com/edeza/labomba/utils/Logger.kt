package com.edeza.labomba.utils

import android.util.Log
import com.edeza.labomba.BuildConfig

/**
 * Objeto de utilidad para registrar mensajes de depuración.
 * Solo registra logs en compilaciones DEBUG para evitar exponer información sensible en producción.
 */

object Logger {
    fun debug(tag: String, message: String, exception: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.d(tag, message, exception)
    }

    fun error(tag: String, message: String, exception: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, message, exception)
    }
}