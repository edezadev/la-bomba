package com.example.labombav2.controller.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemPenaltyBinding
import com.example.labombav2.model.PenaltyModel

class PenaltyAdapter(private val items: List<PenaltyModel>) :
    RecyclerView.Adapter<PenaltyAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPenaltyBinding):
        RecyclerView.ViewHolder(binding.root) {
        val rbPenalty = binding.rbPenalty
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPenaltyBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.rbPenalty.text = item.name
//        holder.cbPenalty.setOnCheckedChangeListener { checkbox, isChecked ->
//            clearChecked(holder) //primero poner todos los valores a false (a nivel de lista)
//            //cambiar a true solo el seleccionado
//            item.isChecked = isChecked
//            holder.cbPenalty.isChecked = isChecked
//            Log.e("ESTADOS", items.toString())
//
//        }
    }

    /*private fun clearChecked(holder: ViewHolder) {
        for (item in items) {
            if (item.isChecked) {
                item.isChecked = false
            }
            holder.cbPenalty.isChecked = false
        }
    }*/
}