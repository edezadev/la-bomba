package com.example.labombav2.controllers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemPlayersResultsBinding
import com.example.labombav2.models.LoserModel

class PlayerResultsAdapter (private var items: MutableList<LoserModel>
): RecyclerView.Adapter<PlayerResultsAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPlayersResultsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvPlayer = binding.tvPlayer
        val tvPoints = binding.tvPoints
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlayersResultsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((binding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvPlayer.text = item.player.name
        holder.tvPoints.text = item.points.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}