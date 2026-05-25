package com.edeza.labomba.controllers.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.edeza.labomba.R
import com.edeza.labomba.databinding.ActivitySettingsBinding
import com.edeza.labomba.utils.BaseActivity
import com.edeza.labomba.controllers.fragments.PenaltyFragment
import com.edeza.labomba.controllers.fragments.TimerFragment
import com.edeza.labomba.utils.GameSession
import com.edeza.labomba.utils.setupInsets
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : BaseActivity() {
    private var binding: ActivitySettingsBinding? = null
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnNext: MaterialButton
    private var currentFragment: Fragment? = null //para validar el ultimo fragmento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            appBarLayout = it.appBarLayout
            toolbar = it.toolbar
            btnNext = it.btnNext
        }
        appBarLayout.setupInsets()
        setSupportActionBar(toolbar)
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
                    if (GameSession.hasChanges()) {
                        showAlertExit()
                    } else {
                        finish()
                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertExit() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_alert))
            .setMessage(getString(R.string.message_exit_settings))
            .setPositiveButton(getString(R.string.exit)) {dialog, _ ->
                GameSession.reset()
                finish()
            }
            .setNegativeButton(getString(R.string.action_negative), null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}