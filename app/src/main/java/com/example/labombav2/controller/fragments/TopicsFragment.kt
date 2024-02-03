package com.example.labombav2.controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.labombav2.R
import com.example.labombav2.controller.activities.SettingsActivity
import com.example.labombav2.databinding.FragmentTopicsBinding

class TopicsFragment : Fragment() {
    private var binding: FragmentTopicsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicsBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity

        activity?.updateView(this, getString(R.string.topics_name))
        return view
    }
}