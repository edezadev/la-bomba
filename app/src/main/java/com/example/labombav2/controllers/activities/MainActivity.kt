package com.example.labombav2.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.labombav2.R
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.databinding.ActivityMainBinding
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null

    //  escucha cambios en el estado de la autenticación.
    private lateinit var stateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        FirebaseAuthManager.getAuthToken {}
        initializeStateListener()

//        Inicializar el SDK Google Mobile Ads en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity)
        }

        binding?.btnStart?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding?.btnInstructions?.setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }
    }

    //  Crear el escuchador de estados
    private fun initializeStateListener() {
        stateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                Log.d("UserFound", "User located in Firebase")
            } else {
//                Llamamos a la función y espramos el resultado (true/false)
                FirebaseAuthManager.createUserAnonymously { success ->
                    /*Si falló la creación (devuelve false) por internet, se muestra el dialogo
                     que bloquea el juego*/
                    if (!success)  showBlockingErrorDialog()
                }
            }
        }
    }

    private fun showBlockingErrorDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_connection_error))
            .setMessage(getString(R.string.message_connection_required))
            .setPositiveButton(getString(R.string.action_retry)) {_ ,_ ->
                initializeStateListener() // Reintenta el proceso
            }
            .setCancelable(false) // Evita que el usuario lo cierre tocando fuera
            .show()
    }

    override fun onStart() {
//      comenzar a escuchar los estados de la autenticación
        super.onStart()
        FirebaseAuthManager.auth.addAuthStateListener(stateListener)
    }

    override fun onStop() {
        super.onStop()
        /* Desvincular el stateListener para evitar fugas de memoria cuando la
         * actvidad está en segundo plano */
        FirebaseAuthManager.auth.removeAuthStateListener(stateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}