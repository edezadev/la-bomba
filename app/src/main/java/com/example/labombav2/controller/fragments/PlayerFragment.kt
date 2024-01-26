package com.example.labombav2.controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.FragmentAddPlayerBinding
import com.example.labombav2.controller.activities.SettingsActivity
import com.example.labombav2.controller.adapters.PlayerAdapter
import com.example.labombav2.controller.dialogs.AddPlayerBottomSheet
import com.example.labombav2.model.PlayerModel
import com.example.labombav2.utils.OnPlayerInsertedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerFragment : Fragment(), OnPlayerInsertedListener {
    private var binding: FragmentAddPlayerBinding? = null
    private var adapter: PlayerAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var recyclerPlayer: RecyclerView
    private lateinit var fabAddPlayer: FloatingActionButton
    private var listPlayers: MutableList<PlayerModel> = mutableListOf(
        PlayerModel(name = "José"),
        PlayerModel(name = "Andrea"),
        PlayerModel(name = "Estefano")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlayerBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        recyclerPlayer = binding?.recyclerPlayer!!
        fabAddPlayer = binding?.fabAddPlayer!!

        setupRecyclerView()

        activity?.updateView(this, getString(R.string.players_name))

        fabAddPlayer.setOnClickListener{ showAddPlayer() }

        return view
    }

    private fun setupRecyclerView() {
        adapter = PlayerAdapter(listPlayers)
        recyclerPlayer.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerPlayer.adapter = adapter
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
        listPlayers.add(newPlayer)
        adapter?.notifyItemInserted(listPlayers.size - 1 )
    }
}