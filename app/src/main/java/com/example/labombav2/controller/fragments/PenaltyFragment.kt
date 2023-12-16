package com.example.labombav2.controller.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.labombav2.utils.Constants
import com.example.labombav2.utils.FirebaseAuthManager
import com.example.labombav2.utils.FirestoreDatabaseManager
import com.example.labombav2.utils.OnPenaltyInsertedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PenaltyFragment : Fragment(), OnPenaltyInsertedListener {
    private var binding: FragmentPenaltyBinding? = null
    private var adapter: PenaltyAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var recyclerPenalty: RecyclerView
    private lateinit var fabAddPenalty: FloatingActionButton
    private var listPenalties: MutableList<PenaltyModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View? {

        binding = FragmentPenaltyBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity//obtener la actividad
        recyclerPenalty = binding?.recyclerPenalty!!
        fabAddPenalty = binding?.fabAddPenalty!!

        addDataRecyclerView()

        activity?.let {
            it.updateView(this, getString(R.string.penalty_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(AddPlayerFragment()) }
        fabAddPenalty.setOnClickListener { showAddPenalty() }

        return view
    }

    private fun addDataRecyclerView() {
//      limpiar la lista antes de agregar nuevos elementos cunando se crea este fragmento
        listPenalties.clear()

        FirebaseAuthManager.getUid {
            FirestoreDatabaseManager.getPenalties(it) { penalty ->
                onPenaltyInserted(penalty)

            }
        }
        recyclerPenalty.layoutManager  = LinearLayoutManager(activity?.applicationContext)
        adapter = PenaltyAdapter(listPenalties)
        recyclerPenalty.adapter = adapter
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
        listPenalties.add(newPenalty)
        adapter!!.notifyItemInserted(listPenalties.size - 1)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuthManager.getUid {
            FirestoreDatabaseManager.userRef.document(it).collection(Constants.PENALTIES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("ListenerFailed", "Failed to listen penalties.", error)
                        return@addSnapshotListener
                    }

                    val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                        "Local"
                    } else {
                        "Server"
                    }

                    if (snapshot != null) {
                        Log.d("SNAPSHOT", "$source data: ${snapshot.metadata}")
                    } else {
                        Log.d("ERROR", "$source data: null")
                    }
                }
        }
    }
}