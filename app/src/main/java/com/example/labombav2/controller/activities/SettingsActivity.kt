package com.example.labombav2.controller.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivitySettingsBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.controller.fragments.AddPlayerFragment
import com.example.labombav2.controller.fragments.PenaltyFragment
import com.example.labombav2.utils.FirebaseAuthManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SettingsActivity : BaseActivity() {
    private var binding: ActivitySettingsBinding? = null
    private lateinit var btnNext: MaterialButton
    private var currentFragment: Fragment? = null //para validar el ultimo fragmento

    //  escucha cambios en el estado de la autenticación.
    private lateinit var stateListener: FirebaseAuth.AuthStateListener
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initializeStateListener()

        btnNext = binding?.btnNext!!
//      Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //mostrar el botón de navegación

//      Primer fragmento
        addFragment(PenaltyFragment())
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment,fragment, fragment.toString())
            .addToBackStack(null)
            .commit()

        currentFragment = fragment
    }

    fun updateView(fragment: Fragment, title: String) {
//      Configurar tÍtulo del fragmento
        supportActionBar?.title = title

//      actualizar visibilidad del botón según el Fragmento
        currentFragment = fragment

        if (::btnNext.isInitialized) {
            if (currentFragment is AddPlayerFragment) {
                btnNext.visibility = View.GONE
            }else {
                btnNext.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val fragmentManager = supportFragmentManager
                /* si hay más de un fragmento en la pila, se usa popBackStack para ir atrás entre
                * fragmentos, de lo contrario se regresa a la activity anterior */
                if (fragmentManager.backStackEntryCount > 1)  {
                    fragmentManager.popBackStack()
                }else {
                    finish()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //  Crear el escuchador de estados
    private fun initializeStateListener() {
        stateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
            if (user != null) {
                Log.d("UserFound", "User located in Firebase")
            } else {
                FirebaseAuthManager.createUserAnonymously()
            }
        }
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