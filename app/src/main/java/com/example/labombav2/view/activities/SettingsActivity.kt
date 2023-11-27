package com.example.labombav2.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivitySettingsBinding
import com.example.labombav2.util.BaseActivity
import com.example.labombav2.view.fragments.PenaltyFragment

class SettingsActivity : BaseActivity() {
    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//      Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //mostrar el botón de navegación

//      Primer fragment
        addFragment(PenaltyFragment())
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment,fragment)
            .addToBackStack(null)
            .commit()
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}