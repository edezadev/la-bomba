package com.example.labombav2.controller.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemPenaltyBinding
import com.example.labombav2.model.PenaltyModel

class PenaltyAdapter(private val items: List<PenaltyModel>) :
    RecyclerView.Adapter<PenaltyAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemPenaltyBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbPenalty = binding.cbPenalty
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
        holder.cbPenalty.text = item.name
        holder.cbPenalty.isChecked = item.isChecked

        holder.cbPenalty.setOnClickListener {
            val checked = (it as CheckBox).isChecked //guardamos el estado actual al hacer click
            updateChecked(position, checked)

            //cambiar a true solo el seleccionado
            item.isChecked = holder.cbPenalty.isChecked
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateChecked(position: Int, checked: Boolean) {
    /* items.indices: proporciona un rango de índices de la lista, y poder realizar operaciones
    * basadas en esos índices */
        for (index in items.indices) {
            if (index != position) {
//              cambiar a false el estado del item que es distinto al que fue accionado
                items[index].isChecked = false
            }else{
                items[index].isChecked = checked
            }
        }
        notifyDataSetChanged()
    }
}