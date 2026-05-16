package com.edeza.labomba.controllers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edeza.labomba.databinding.ItemPageTopicsBinding
import com.edeza.labomba.models.TopicModel
/**
 * Manejar la lista de las páginas con su respectiva lista de temas, y pasar cada página con su
 * lista de temas a Topic Adapter
 **/
class PageTopicsAdapter(
    private val items:MutableList<MutableList<TopicModel>>,
    private var fragmentManager: FragmentManager
) :
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
        val adapter = TopicAdapter(page, fragmentManager)
        holder.recyclerTopic.adapter = adapter
    }
}