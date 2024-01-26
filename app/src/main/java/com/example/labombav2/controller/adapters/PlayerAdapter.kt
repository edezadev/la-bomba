package com.example.labombav2.controller.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemPlayerBinding
import com.example.labombav2.model.PlayerModel

class PlayerAdapter(private var items: MutableList<PlayerModel>) :
    RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPlayerBinding):
        RecyclerView.ViewHolder(binding.root) {
        val tvPlayer = binding.tvPlayer
        lateinit var uid: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerAdapter.ViewHolder {
        val binding = ItemPlayerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PlayerAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.tvPlayer.text = item.name
    }
}