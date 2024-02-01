package com.example.labombav2.controller.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.FragmentAddPlayerBinding
import com.example.labombav2.controller.activities.SettingsActivity
import com.example.labombav2.controller.adapters.PlayerAdapter
import com.example.labombav2.controller.dialogs.AddPlayerBottomSheet
import com.example.labombav2.model.PlayerModel
import com.example.labombav2.utils.FirebaseAuthManager
import com.example.labombav2.utils.OnPlayerInsertedListener
import com.example.labombav2.utils.PlayerDbManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class PlayerFragment : Fragment(), OnPlayerInsertedListener {
    private var binding: FragmentAddPlayerBinding? = null
    private var adapter: PlayerAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var tvNoPlayers: TextView
    private lateinit var recyclerPlayer: RecyclerView
    private lateinit var fabAddPlayer: FloatingActionButton
    private var listPlayers: MutableList<PlayerModel> = mutableListOf()

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlayerBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        binding?.let {
            recyclerPlayer = it.recyclerPlayer
            fabAddPlayer = it.fabAddPlayer
            tvNoPlayers = it.tvNoPlayers
        }

        setupRecyclerView()
//      Listar todos los jugadores
        FirebaseAuthManager.getUid { uid ->
            listenerRegistration = PlayerDbManager.getPlayersListener(uid) {
                if(it.isEmpty()) {
                    tvNoPlayers.visibility = View.VISIBLE
                } else {
                    tvNoPlayers.visibility = View.GONE
                    addDataRecyclerView(it)
                }
            }
        }

        activity?.updateView(this, getString(R.string.players_name))

        fabAddPlayer.setOnClickListener{ showAddPlayer() }

        return view
    }

    private fun setupRecyclerView() {
        adapter = PlayerAdapter(listPlayers, parentFragmentManager)
        recyclerPlayer.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerPlayer.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addDataRecyclerView(players: MutableList<PlayerModel>) {
        listPlayers.clear()
        listPlayers.addAll(players)
        adapter?.notifyDataSetChanged()
    }

    private fun showAddPlayer() {
        val bottomSheet = AddPlayerBottomSheet()
        /* Antes de mostrar el bottomSheet, asignar el fragmento actual como listener, para
        * establecer la comunicación entre el bottomSheet con el fragmento al insertar datos, a
        * través de la interfaz */
        bottomSheet.setOnDataInsertedListener(this)
        bottomSheet.show(parentFragmentManager.beginTransaction(), AddPlayerBottomSheet.TAG)
    }

    override fun onPlayerInserted(newPlayer: PlayerModel) {
        FirebaseAuthManager.getUid { uid ->
            PlayerDbManager.createPlayer(uid, newPlayer)
        }
    }

    override fun onStop() {
        super.onStop()
        if(::listenerRegistration.isInitialized){
            listenerRegistration.remove()
        }
    }
}