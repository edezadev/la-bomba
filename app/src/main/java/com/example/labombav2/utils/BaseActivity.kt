package com.example.labombav2.utils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.labombav2.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/* Todas las activies deberán extender de BaseActivity en lugar de AppCompatActivity para que
 * esta configuración de las barras del sistema esté en todas las pantallas */

abstract class BaseActivity: AppCompatActivity() {
    private var doubleBackPressed = false
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Habilitar edge to edge, para la función de pantalla completa con barras de estado
         * transparentes, REQUIERE android 10 (API 29) */
        enableEdgeToEdge()
        /* Ajusta el espacio alrededor de la vista principal de la app para que no se superponga
         * con las barras de sistema */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*Anular la navegación back del sistema, doble toque para salir de la app*/
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                Segundo toque dentro de 2 seg. ahora es true y sale de la app
                if (doubleBackPressed) {
                    finishAffinity()
                    return
                }
//                Primer toque es ture y muestra el Toast
                doubleBackPressed = true
                Toast.makeText(this@BaseActivity, getString(R.string.action_back), Toast.LENGTH_LONG).show()
//                Tras 2 seg. se vuelve a poner en false si el usuario no presiona de nuevo
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackPressed = false
                }, 2000)
            }

        })
    }

    fun showLoading() {
        if (loadingDialog == null) {
            val builder = MaterialAlertDialogBuilder(this, R.style.LoadingDialogStyle)
            val view = layoutInflater.inflate(R.layout.dialog_loading, null)
            builder.setView(view)
            builder.setCancelable(false)
            loadingDialog = builder.create()
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        if (loadingDialog?.isShowing == false) loadingDialog?.show()
    }

    fun dismissLoading() {
        if (loadingDialog?.isShowing == true) loadingDialog?.dismiss()
    }

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

// Funciones de extensión, para fragments
fun Fragment.showLoading() {
    (activity as? BaseActivity)?.showLoading()
}

fun Fragment.dismissLoading() {
    (activity as? BaseActivity)?.dismissLoading()
}