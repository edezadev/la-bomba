package com.edeza.labomba.controllers.activities

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import com.edeza.labomba.R
import com.edeza.labomba.utils.BaseActivity
import com.edeza.labomba.databinding.ActivityMainBinding
import com.edeza.labomba.config.auth.FirebaseAuthManager
import com.edeza.labomba.utils.Logger
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null
    private var errorDialog: AlertDialog? = null
    // Bandera para controlar el registro del listener
    private var shouldRegisterListener = true
    //  escucha cambios en el estado de la autenticación.
    private lateinit var stateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initializeStateListener()

//        Inicializar el SDK Google Mobile Ads en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity)
        }

        binding?.btnStart?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding?.btnInstructions?.setOnClickListener {
            startActivity(Intent(this, InstructionsActivity::class.java))
        }
    }
    private fun checkNetworkAndShowWarning() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

//        Verificamos si hay una red activa y si esa red tiene capacidad de internet
        val isOnline = capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        if (!isOnline) {
            binding?.let {
                Snackbar.make(
                    it.root,
                    getString(R.string.message_offline),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.action_confirm)) {}.show()
            }
        }
    }

    private fun initializeStateListener() {
        stateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                dismissLoading()
                checkNetworkAndShowWarning()
                Logger.debug("UserFound", "User located in Firebase")
            } else if (shouldRegisterListener) {
                showLoading() //Mostrar carga antes de llamar a Firebase
                FirebaseAuthManager.createUserAnonymously { success ->
                    Handler(mainLooper).postDelayed({
                        dismissLoading()
                        if (!success) {
                            shouldRegisterListener = false
                            // Detenemos la escucha automática
                            FirebaseAuthManager.auth.removeAuthStateListener(stateListener)
                            showBlockingErrorDialog()
                        }
                    }, 1000)
                }
            }
        }
    }

    private fun showBlockingErrorDialog() {
        if (errorDialog?.isShowing == true) return

        errorDialog = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_connection_error))
            .setMessage(getString(R.string.message_connection_required))
            .setPositiveButton(getString(R.string.action_retry)) {_ ,_ ->
                shouldRegisterListener = true // Reactivamos la bandera
                showLoading()
                // Reiniciamos la escucha
                FirebaseAuthManager.auth.addAuthStateListener(stateListener)
                // Pertmitir que se pueda crear uno nuevo después de presionar reintentar
                errorDialog = null
            }
            .setCancelable(false)
            .show()
    }

    override fun onStart() {
        super.onStart()
        // Si la bandera es true (no hay dialogo visible), activamos la escucha
        if (shouldRegisterListener) {
            FirebaseAuthManager.auth.addAuthStateListener(stateListener)
        }
    }

    override fun onStop() {
        super.onStop()
        /* Desvincular el stateListener para evitar fugas de memoria cuando la
         * actvidad está en segundo plano */
        FirebaseAuthManager.auth.removeAuthStateListener(stateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        errorDialog?.dismiss() // Cerrar si la actividad muere
        errorDialog = null
        binding = null
    }
}