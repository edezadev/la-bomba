package com.example.labombav2.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivityStartGameBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.utils.GameSession
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class StartGameActivity : BaseActivity() {
    private var binding: ActivityStartGameBinding? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTime: TextView
    private lateinit var tvTopicName: TextView
    private var round = 0
    private var timeMillisecond: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            toolbar = it.toolbar
            tvTime = it.tvTime
            tvTopicName = it.tvTopicName
        }
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.primary))
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exit -> {
                    showAlertExitGame()
                    true
                }
                else -> false
            }
        }

        updateTitleAndTopics()
        setTimerSound()
    }

    private fun updateTitleAndTopics() {
        val titleRounds = getString(R.string.titleRounds)
        toolbar.title = "$titleRounds ${round + 1}"
        tvTopicName.text = GameSession.topics[round].name
    }

    private fun setTimerSound() {
        timeMillisecond = GameSession.time?.toLong()

        updateTimer()
    }

    private fun updateTimer() {
        val minutes = (timeMillisecond?.div(60000))?.toInt()
        val seconds = ((timeMillisecond?.rem(60000))?.div(1000))?.toInt()

        val textTimer = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds)
        tvTime.text = textTimer
    }

    private fun showAlertExitGame() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_alert))
            .setMessage(getString(R.string.message_exit_game))
            .setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                Toast.makeText(this, "Aqui va anuncio", Toast.LENGTH_SHORT).show()
                GameSession.reset()
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            }
            .setNegativeButton(getString(R.string.action_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}