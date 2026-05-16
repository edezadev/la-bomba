package com.edeza.labomba.controllers.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edeza.labomba.R
import com.edeza.labomba.controllers.fragments.TopicsFragment
import com.edeza.labomba.databinding.ItemPageIndicatorBinding
import com.edeza.labomba.utils.listeners.OnCurrentPageListener

class PageIndicatorAdapter (
    private var currentPage: Int,
    private val totalPage: Int,
    private val onPageClick: (Int) -> Unit) : OnCurrentPageListener,
    RecyclerView.Adapter<PageIndicatorAdapter.ViewHolder>() {

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
        val topicsFragment = TopicsFragment()
        topicsFragment.setOnDataChangeListener(this)
        holder.tvIndicatorPage.text = (position+1).toString()
//      Enviar la posición para cambiar la página
        holder.tvIndicatorPage.setOnClickListener { onPageClick(position) }

        if (position == currentPage){
            holder.tvIndicatorPage.setTextColor(
                ContextCompat.getColor(holder.tvIndicatorPage.context,R.color.surface))
            holder.tvIndicatorPage.setBackgroundResource(R.drawable.background_selected)
        } else {
            holder.tvIndicatorPage.setTextColor(
                ContextCompat.getColor(holder.tvIndicatorPage.context,R.color.primary))


            holder.tvIndicatorPage.setBackgroundResource(R.drawable.background_edge)
        }
    }

    override fun onCurrentPageChange(currentPage: Int) {
//        Manejar el valor de current page
        Log.e("CURRENT", currentPage.toString())
        this.currentPage = currentPage
    }
}