package com.example.labombav2.controller.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.FragmentPenaltyBinding
import com.example.labombav2.model.PenaltyModel
import com.example.labombav2.controller.activities.SettingsActivity
import com.example.labombav2.controller.adapters.PenaltyAdapter
import com.example.labombav2.controller.dialogs.AddPenaltyBottomSheet
import com.example.labombav2.utils.FirebaseAuthManager
import com.example.labombav2.utils.FirestoreDatabaseManager
import com.example.labombav2.utils.OnPenaltyInsertedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class PenaltyFragment : Fragment(), OnPenaltyInsertedListener {
    private var binding: FragmentPenaltyBinding? = null
    private var adapter: PenaltyAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var recyclerPenalty: RecyclerView
    private lateinit var fabAddPenalty: FloatingActionButton
    private var listPenalties: MutableList<PenaltyModel> = mutableListOf()

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View? {

        binding = FragmentPenaltyBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity//obtener la actividad
        recyclerPenalty = binding?.recyclerPenalty!!
        fabAddPenalty = binding?.fabAddPenalty!!

        setupRecyclerView()
        FirebaseAuthManager.getUid {uid ->
            listenerRegistration = FirestoreDatabaseManager.getPenaltiesListener(uid) {
                addDataRecyclerView(it)
            }
        }

        activity?.let {
            it.updateView(this, getString(R.string.penalty_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(AddPlayerFragment()) }
        fabAddPenalty.setOnClickListener { showAddPenalty() }

        return view
    }

    private fun setupRecyclerView() {
        adapter = PenaltyAdapter(listPenalties)
        recyclerPenalty.layoutManager  = LinearLayoutManager(activity?.applicationContext)
        recyclerPenalty.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addDataRecyclerView(penalties: MutableList<PenaltyModel>) {
        listPenalties.clear()
        listPenalties.addAll(penalties)
        adapter?.notifyDataSetChanged()
    }

    private fun showAddPenalty() {
        val bottomSheet = AddPenaltyBottomSheet()
        /*Antes de mostrar el bottomSheet, asignamos el fragmento como OnPenaltyinsertedListener.
        * Esto garantiza que cuando el bottomSheet necesite insertar datos, pueda comunicarse con
        * el fragmento a través de la interfaz*/
        bottomSheet.setOnDataInsertedListener(this)
        bottomSheet.show(parentFragmentManager.beginTransaction(), AddPenaltyBottomSheet.TAG)
    }

    override fun onPenaltyInserted(newPenalty: PenaltyModel) {
        FirebaseAuthManager.getUid { uid ->
            FirestoreDatabaseManager.createPenalty(uid, newPenalty)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::listenerRegistration.isInitialized){
            listenerRegistration.remove()
        }
    }
}