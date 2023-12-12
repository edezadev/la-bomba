package com.example.labombav2.controller.activities

import android.content.Intent
import android.os.Bundle
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.databinding.ActivityMainBinding
import com.example.labombav2.utils.FirebaseAuthManager

class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        FirebaseAuthManager.getAuthToken {}

        binding?.btnStart?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding?.btnInstructions?.setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}