package com.example.labombav2.controller.fragments

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
import com.google.android.material.button.MaterialButton

class PenaltyFragment : Fragment() {
    private var binding: FragmentPenaltyBinding? = null
    private var adapter: PenaltyAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var recyclerPenalty: RecyclerView
    private val listPenalty = listOf(
        PenaltyModel("Bailar", false),
        PenaltyModel("Cantar", false),
        PenaltyModel("Beber", false),
        PenaltyModel("Contar un chiste", false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPenaltyBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity//obtener la actividad
        recyclerPenalty = binding?.recyclerPenalty!!

        addData()

        activity?.let {
            it.updateView(this, getString(R.string.penalty_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(AddPlayerFragment()) }

        return view
    }

    private fun addData() {
        recyclerPenalty.layoutManager  = LinearLayoutManager(activity?.applicationContext)
        adapter = PenaltyAdapter(listPenalty)
        recyclerPenalty.adapter = adapter
    }
}