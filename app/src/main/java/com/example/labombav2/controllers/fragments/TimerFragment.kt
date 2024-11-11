package com.example.labombav2.controllers.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.example.labombav2.R
import com.example.labombav2.controllers.activities.SettingsActivity
import com.example.labombav2.databinding.FragmentTimerBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton

class TimerFragment : Fragment() {
    private var binding: FragmentTimerBinding? = null
    private lateinit var btnStartGame: MaterialButton
    private lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        binding?.let {
            radioGroup = it.radioGroup
        }

        activity?.updateView(this, getString(R.string.timer_name))

        getSelectedTime()
        return view
    }

    private fun getSelectedTime() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            changeStyle(checkedId)
            val selectedValue = radioGroup.findViewById<MaterialRadioButton>(checkedId)
            Log.e("valor del tiempo", selectedValue.text.toString())
        }
    }

    private fun changeStyle(checkedId: Int) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i)
            if (radioButton is MaterialRadioButton) {
                val colorRes = if (radioButton.id != checkedId) R.color.primary else R.color.onPrimary
                radioButton.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
            }
        }
    }

}