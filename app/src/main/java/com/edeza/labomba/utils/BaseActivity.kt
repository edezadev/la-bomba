package com.edeza.labomba.utils

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.edeza.labomba.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/* Todas las activies deberán extender de BaseActivity en lugar de AppCompatActivity para que
 * esta configuración de las barras del sistema esté en todas las pantallas */
abstract class BaseActivity: AppCompatActivity() {
    private var doubleBackPressed = false
    private var loadingDialog: AlertDialog? = null
    private val doubleBackHandler = Handler(Looper.getMainLooper())
    private val resetDoubleBackRunnable = Runnable { doubleBackPressed = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupImmersiveMode()


        /*Anular la navegación back del sistema, doble toque para salir de la app*/
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                Segundo toque dentro de 2 seg. ahora es true y sale de la app
                if (doubleBackPressed) {
                    finishAffinity()
                    return
                }
//                Primer toque es true y muestra el Toast
                doubleBackPressed = true
                Toast.makeText(this@BaseActivity, getString(R.string.action_back), Toast.LENGTH_LONG).show()
//                Tras 2 seg. se vuelve a poner en false si el usuario no presiona de nuevo
                doubleBackHandler.postDelayed(resetDoubleBackRunnable, 2000)
            }
        })
    }
    @Suppress("DEPRECATION")
    private fun setupImmersiveMode() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Permitir dibujar en el área del notch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val controller = WindowCompat.getInsetsController(window, window.decorView)

        // Configura el comportamiento: las barras aparecen al deslizar y se ocultan solas
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Oculta tanto la barra de estado como la de navegación
        controller.hide(WindowInsetsCompat.Type.systemBars())
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
        try {
            // Validar que la actividad siga activa para evitar BadTokenException
            if (!isFinishing && !isDestroyed && loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }
        } catch (e: Exception) {
            Logger.error("BaseActivity", "Error while closing the loading dialog", e)
        }finally {
            loadingDialog = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupImmersiveMode()
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        setupImmersiveMode() // Re-oculta si el sistema intentó mostrarlas al cambiar de modo oscuro
    }

    override fun onDestroy() {
        // Cancelar procesos pendientes para evitar fugas de memoria y errores de referencia
        doubleBackHandler.removeCallbacks(resetDoubleBackRunnable)
        dismissLoading()
        super.onDestroy()
    }
}

/** Funciones de extensión, para fragments **/
fun Fragment.showLoading() {
    (activity as? BaseActivity)?.showLoading()
}

fun Fragment.dismissLoading() {
    (activity as? BaseActivity)?.dismissLoading()
}

fun View.setupInsets() {
    /* Agrega automáticamente un padding superior igual al tamaño de la barra de estado/notch
    para evitar que el contenido quede tapado. */
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()).top
        v.setPadding(0, topInset, 0, 0)
        insets
    }
}