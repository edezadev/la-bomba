package com.example.labombav2.controllers.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InstructionsAdapter(
    private var slides: MutableList<Fragment> = mutableListOf(),
    fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return slides.size
    }

    override fun createFragment(position: Int): Fragment {
        return slides[position]
    }

    /*Métodoo que agrega la vista de cada slide hecha en un fragment*/
    fun addFragment(fragment: Fragment) {
        slides.add(fragment)
    }
}