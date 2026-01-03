package com.example.labombav2.controllers.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.controllers.adapters.PlayerResultsAdapter
import com.example.labombav2.databinding.ActivityResultsBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.utils.GameSession
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ResultsActivity : BaseActivity() {
    private var binding: ActivityResultsBinding? = null
    private var adapter: PlayerResultsAdapter? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerResults: RecyclerView
    private lateinit var btnRematch: MaterialButton
    private lateinit var btnGoToHome: MaterialButton
    private lateinit var groupButtonChain: Group
    private lateinit var btnGoToHomeSingle: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            toolbar = it.toolbar
            recyclerResults = it.recyclerResults
            btnRematch = it.btnRematch
            btnGoToHome = it.btnGoToHome
            groupButtonChain = it.groupButtonChain
            btnGoToHomeSingle = it.btnGoToHomeSingle
        }

        toolbar.title = getString(R.string.results_title)
        setupRecyclerView()



//        Creas un solo objeto lambda en memoria y pasas su referencia a ambos listeners.
        val goToHomeAction = { goToHome() }
        btnGoToHome.setOnClickListener { goToHomeAction }
        btnGoToHomeSingle.setOnClickListener { goToHomeAction }
    }

    private fun setupRecyclerView() {
        adapter = PlayerResultsAdapter(GameSession.loser)
        recyclerResults.layoutManager = LinearLayoutManager(this)
        recyclerResults.adapter = adapter
    }

    private fun goToHome() {
        GameSession.reset() //Limpiar todos los datos del juego
        startActivity(Intent(this, MainActivity::class.java))
    }
}