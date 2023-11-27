package com.example.labombav2.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.labombav2.R
import com.example.labombav2.databinding.FragmentPenaltyBinding

class PenaltyFragment : Fragment() {
    private var binding: FragmentPenaltyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPenaltyBinding.inflate(inflater, container, false)
        val view = binding?.root

        val activity = activity
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.title = getString(R.string.penalty_name)
        }

        return view
    }
}