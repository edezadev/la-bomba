package com.example.labombav2.controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.labombav2.R
import com.example.labombav2.databinding.FragmentAddPlayerBinding
import com.example.labombav2.controller.activities.SettingsActivity
import com.google.android.material.button.MaterialButton

class AddPlayerFragment : Fragment() {
    private var binding: FragmentAddPlayerBinding? = null
    private lateinit var btnNext: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlayerBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity

        activity?.let {
//            btnNext = it.findViewById(R.id.btnNext)
            it.updateView(this, getString(R.string.players_name))
        }

        return view
    }
}