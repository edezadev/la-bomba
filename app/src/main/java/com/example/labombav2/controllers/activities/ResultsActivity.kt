package com.example.labombav2.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.PlayerDbManager
import com.example.labombav2.controllers.adapters.PlayerResultsAdapter
import com.example.labombav2.databinding.ActivityResultsBinding
import com.example.labombav2.models.LoserModel
import com.example.labombav2.utils.AdsManager
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.utils.GameSession
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ResultsActivity : BaseActivity() {
    private var binding: ActivityResultsBinding? = null
    private var adapter: PlayerResultsAdapter? = null
    private var potentialLosers = listOf<LoserModel>()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvLoserAndPenalty: TextView
    private lateinit var recyclerResults: RecyclerView
    private lateinit var btnRematch: MaterialButton
    private lateinit var btnGoToHome: MaterialButton
    private lateinit var groupButtonChain: Group
    private lateinit var btnGoToHomeSingle: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        AdsManager.init(this) //Inicializar la carga del anuncio
        binding?.let {
            toolbar = it.toolbar
            tvLoserAndPenalty = it.tvLoserAndPenalty
            recyclerResults = it.recyclerResults
            btnRematch = it.btnRematch
            btnGoToHome = it.btnGoToHome
            groupButtonChain = it.groupButtonChain
            btnGoToHomeSingle = it.btnGoToHomeSingle
        }

        toolbar.title = getString(R.string.results_title)
        setupRecyclerView()

        showLoserAndPenalty()

//        Creas un solo objeto lambda en memoria y pasas su referencia a ambos listeners.
        val goToHomeAction = { goToHome() }
        btnRematch.setOnClickListener { goToRematch() }
        btnGoToHome.setOnClickListener { goToHomeAction() }
        btnGoToHomeSingle.setOnClickListener { goToHomeAction() }
    }

    private fun showLoserAndPenalty() {
        val loserList = GameSession.loser

//        Busca el puntaje más alto
        val highestScore = loserList.maxOf { it.points }

//        Filtra la lista para encontrar a todos los que tienen el puntaje más alto. Crea una lista
        potentialLosers = loserList.filter { it.points == highestScore }

        val message = if (potentialLosers.size == 1) {
//            No hay empate, solo un perdedor
            val loser = potentialLosers.first()
            if (GameSession.penalty == null) {
                getString(R.string.message_loser, loser.player.name)
            } else {
                "${getString(R.string.message_loser, loser.player.name)} " +
                        "y tu castigo es ${GameSession.penalty?.name}"
            }
        } else { //Hay empate se muestran la opcion de empate
            getString(R.string.message_tie)
        }

        tvLoserAndPenalty.text = message

        val showRematch = potentialLosers.size > 1
        if (showRematch) {
            groupButtonChain.visibility = View.VISIBLE
            btnGoToHomeSingle.visibility = View.GONE
        } else {
            groupButtonChain.visibility = View.GONE
            btnGoToHomeSingle.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = PlayerResultsAdapter(GameSession.loser)
        recyclerResults.layoutManager = LinearLayoutManager(this)
        recyclerResults.isNestedScrollingEnabled = false
        recyclerResults.adapter = adapter
    }

    private fun goToHome() {
        AdsManager.showAd(this) {
            GameSession.reset() //Limpiar todos los datos del juego
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun goToRematch() {
        /* .getUid es una operación asíncrona. Se ejecuta en segundo plano y llama a su lambda
        ({ uid = it }) más tarde. Es más seguro poner toda la lógica dentgro del lambda */
        FirebaseAuthManager.getUid { uid ->
            /* Crear un conjunto (Set) con los jugadores que SÍ empataron.
            * .map { it.player } transforma la List<LoserModel> en una List<PlayerModel>.
            * .toSet() la convierte en un conjunto para búsquedas rápidas. */
            val tiedLosers = potentialLosers.map { it.player }.toSet()

    //        Iterar sobre TODOS los jugadores de la sesión original
            for (player in GameSession.players) {
                if (player !in tiedLosers) {
                    PlayerDbManager.deletePlayer(uid, player.id)
                }
            }

            /* Ya que no se llama a reset de GameSession por ser una revancha, solo se limpia
            * directamente la lista de perdedores para que no se duplique con la nueva de la
            * revancha*/
            GameSession.loser.clear()

            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }
}