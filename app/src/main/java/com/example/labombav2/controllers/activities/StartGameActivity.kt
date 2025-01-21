package com.example.labombav2.controllers.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.labombav2.R
import com.example.labombav2.databinding.ActivityStartGameBinding
import com.example.labombav2.utils.BaseActivity
import com.google.android.material.appbar.MaterialToolbar

class StartGameActivity : BaseActivity() {
    private var binding: ActivityStartGameBinding? = null
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            toolbar = it.toolbar
        }
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.primary))
        toolbar.title = "Ronda N° 1"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}