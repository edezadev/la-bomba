package com.example.labombav2.controllers.fragments

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
import com.example.labombav2.databinding.FragmentPenaltyBinding
import com.example.labombav2.models.PenaltyModel
import com.example.labombav2.controllers.activities.SettingsActivity
import com.example.labombav2.controllers.adapters.PenaltyAdapter
import com.example.labombav2.controllers.dialogs.AddPenaltyBottomSheet
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.PenaltyDbManager
import com.example.labombav2.utils.listeners.OnPenaltyInsertedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class PenaltyFragment : Fragment(), OnPenaltyInsertedListener {
    private var binding: FragmentPenaltyBinding? = null
    private var adapter: PenaltyAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var recyclerPenalty: RecyclerView
    private lateinit var tvNoPenalties: TextView
    private lateinit var fabAddPenalty: FloatingActionButton
    private var listPenalties: MutableList<PenaltyModel> = mutableListOf()

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View? {

        binding = FragmentPenaltyBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity//obtener la actividad
        binding?.let {
            recyclerPenalty = it.recyclerPenalty
            tvNoPenalties = it.tvNoPenalties
            fabAddPenalty = it.fabAddPenalty
        }

        setupRecyclerView()
        FirebaseAuthManager.getUid { uid ->
            listenerRegistration = PenaltyDbManager.getPenaltiesListener(uid) {
                if (!isAdded) return@getPenaltiesListener

                if (it.isEmpty()){
                    listPenalties.clear()
                    tvNoPenalties.visibility = View.VISIBLE
                } else {
                    tvNoPenalties.visibility = View.GONE
                    addDataRecyclerView(it)
                }
            }
        }

        activity?.let {
            it.updateView(this, getString(R.string.penalty_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(PlayerFragment()) }
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
            PenaltyDbManager.createPenalty(uid, newPenalty)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::listenerRegistration.isInitialized){
            listenerRegistration.remove()
        }
    }
}