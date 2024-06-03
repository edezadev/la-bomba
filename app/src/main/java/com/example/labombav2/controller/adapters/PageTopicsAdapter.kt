package com.example.labombav2.controller.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemPageTopicsBinding
import com.example.labombav2.model.TopicModel

class PageTopicsAdapter(private val items:MutableList<MutableList<TopicModel>>) :
    RecyclerView.Adapter<PageTopicsAdapter.ViewHolder>(){
    inner class ViewHolder(binding: ItemPageTopicsBinding) : RecyclerView.ViewHolder(binding.root) {
        val recyclerTopic = binding.recyclerTopic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPageTopicsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val page = items[position]
        val layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerTopic.layoutManager = layoutManager
        val adapter = TopicAdapter(page)
        holder.recyclerTopic.adapter = adapter
    }
}