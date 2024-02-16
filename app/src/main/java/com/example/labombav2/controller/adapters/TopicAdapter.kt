package com.example.labombav2.controller.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemTopicBinding
import com.example.labombav2.model.TopicModel

class TopicAdapter(private var items: MutableList<TopicModel>):
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemTopicBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbTopic = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.cbTopic.text = item.name
    }

}