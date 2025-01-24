package com.example.labombav2.controllers.activities

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivityStartGameBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.utils.GameSession
import com.google.android.material.appbar.MaterialToolbar
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}