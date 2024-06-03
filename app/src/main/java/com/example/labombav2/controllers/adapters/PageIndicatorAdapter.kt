package com.example.labombav2.controllers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.ItemPageIndicatorBinding

class PageIndicatorAdapter (
    private val currentPage: Int,
    private val totalPage: Int,
    private val onPageClick: (Int) -> Unit) : RecyclerView.Adapter<PageIndicatorAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPageIndicatorBinding) : RecyclerView.ViewHolder(binding.root){
        val tvIndicatorPage = binding.tvIndicatorPage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPageIndicatorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return totalPage
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvIndicatorPage.text = (position+1).toString()
        holder.tvIndicatorPage.setOnClickListener { onPageClick(position) }

        if (position == currentPage){
            holder.tvIndicatorPage.setTextColor(
                ContextCompat.getColor(holder.tvIndicatorPage.context,R.color.onSurface))
        } else {
            holder.tvIndicatorPage.setTextColor(
                ContextCompat.getColor(holder.tvIndicatorPage.context,R.color.primary))
        }
    }
}