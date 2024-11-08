package com.example.labombav2.controllers.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivitySettingsBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.controllers.fragments.PenaltyFragment
import com.example.labombav2.controllers.fragments.TimerFragment
import com.google.android.material.button.MaterialButton

class SettingsActivity : BaseActivity() {
    private var binding: ActivitySettingsBinding? = null
    private lateinit var btnNext: MaterialButton
    private var currentFragment: Fragment? = null //para validar el ultimo fragmento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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
            if (currentFragment is TimerFragment) {
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
                    Toast.makeText(this,
                        "Aqui va dialogo de confirmación de abandono del juego",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}