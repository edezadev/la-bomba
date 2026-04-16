package com.example.labombav2.controllers.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.labombav2.R
import com.example.labombav2.controllers.activities.SettingsActivity
import com.example.labombav2.databinding.FragmentSliderBinding
import com.example.labombav2.utils.AdsManager
import com.example.labombav2.utils.Constants
import com.google.android.material.button.MaterialButton

class SliderFragment : Fragment() {
    private var binding: FragmentSliderBinding? = null

    private lateinit var tvTitle: TextView
    private lateinit var ivInstruction: ImageView
    private lateinit var btnStart: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSliderBinding.inflate(inflater, container, false)
        val view = binding?.root
        binding?.let {
            tvTitle = it.tvTitle
            ivInstruction = it.ivInstruction
            btnStart = it.btnStart
        }
        AdsManager.init(requireContext())

        if (arguments != null) {
            tvTitle.text = requireArguments().getString(Constants.TITLE)
            ivInstruction.setImageResource(requireArguments().getInt(Constants.IMAGE))

            if (requireArguments().getInt(Constants.IMAGE) == R.drawable.ic_pass_bomb) {
                btnStart.visibility = View.VISIBLE
            }
        }

        btnStart.setOnClickListener {
            AdsManager.showAd(requireActivity()) {
                startActivity(Intent(it.context, SettingsActivity::class.java))
                /* Destruir la activity y eliminarla de la pila de activities, esto con el fin de que al
                * presionar el botón "atrás" de SettingActvity, no regrese a InstructionsActivity sino
                * redirija a MainActivity */
                activity?.finish()
            }
        }

        return view
    }
}