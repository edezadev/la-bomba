package com.example.labombav2.controllers.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.example.labombav2.R
import com.example.labombav2.controllers.activities.SettingsActivity
import com.example.labombav2.controllers.activities.StartGameActivity
import com.example.labombav2.databinding.FragmentTimerBinding
import com.example.labombav2.utils.GameSession
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            btnStartGame = it.btnStartGame
        }

        activity?.updateView(this, getString(R.string.timer_name))

        btnStartGame.setOnClickListener {
            when (dataValidation()) {
                1 -> showAlertDialog(getString(R.string.text_alert_players))
                2 -> showAlertDialog(getString(R.string.text_alert_topics))
                else -> startActivity(Intent(requireContext(), StartGameActivity::class.java))
            }
        }
        getSelectedTime()
        return view
    }

    private fun getSelectedTime() {
        /*Setear el tiempo guardado en GameSession*/
        GameSession.time?.let { time ->
            getRadioButtonId(time).takeIf { it != -1 }?.let { radioButtonId ->
                radioGroup.check(radioButtonId)
                changeStyle(radioButtonId)
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            changeStyle(checkedId)
            val selectedTime = radioGroup.findViewById<MaterialRadioButton>(checkedId)
                .text.toString().toInt()
            GameSession.time = selectedTime * 1000
        }
    }

    private fun getRadioButtonId(milliseconds: Int?): Int {
        return when (milliseconds) {
            30000 -> R.id.rb30sec
            45000 -> R.id.rb45sec
            60000 -> R.id.rb60sec
            90000 -> R.id.rb90sec
            else -> -1
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

    private fun dataValidation(): Int {
        if (GameSession.players.size < 2) {
            return 1
        }

        if (GameSession.topics.size < 5) {
            return 2
        }

        return -1
    }

    private fun showAlertDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_alert))
            .setMessage(message)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}