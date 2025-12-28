package com.example.labombav2.controllers.activities

import android.os.Bundle
import com.example.labombav2.databinding.ActivityResultsBinding
import com.example.labombav2.utils.BaseActivity

class ResultsActivity : BaseActivity() {
    private var binding: ActivityResultsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {

        }

    }
}