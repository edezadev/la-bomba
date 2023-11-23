package com.example.labombav2

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity: AppCompatActivity() {
    private fun hideSystemUiHighR() {
        val windowController = WindowCompat.getInsetsController(window, window.decorView)
        /* Configurar el comportamiento de las barras del sistema: esta opción muestra las barras del
        sistema de forma temporal con gestos. Estas barras se superponen con el contenido, tienen
        cierto grado de transparencia y se oculta automaticamente luego de un breve tiempo */
        windowController.systemBarsBehavior = WindowInsetsControllerCompat
            .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

//          ocultar ambas barras del sistema
        windowController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun hideSystemUiLessR() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                // Código para versiones de Android igual o superior a la 30
                hideSystemUiHighR()
            } else {
                // Código para versiones de Android anteriores a la 30
                hideSystemUiLessR()
            }
        }
    }
}