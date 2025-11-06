package com.example.labombav2.controllers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.databinding.ItemLoserBinding
import com.example.labombav2.models.PlayerModel

class LoserAdapter (
    private var items: MutableList<PlayerModel>,
    private var fragmentManager: FragmentManager
): RecyclerView.Adapter<LoserAdapter.ViewHolder>() {
    private var selectedLoser: Int = RecyclerView.NO_POSITION //Controlar la seleccion de items
    inner class ViewHolder(binding: ItemLoserBinding) :

        RecyclerView.ViewHolder (binding.root){
        val rbLoser = binding.rbLoser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rbLoser.text = items[position].name
//        Solo marca el RB que coincide con el seleccionado
        holder.rbLoser.isChecked = position == selectedLoser
        holder.rbLoser.setOnClickListener {
            val previousSelection = selectedLoser //Guardamos la selección anterior
            selectedLoser = holder.adapterPosition //Actualizamos la nueva selección de perdedor

//            Refresca solo el item anterior y el nuevo
            notifyItemChanged(previousSelection)
            notifyItemChanged(selectedLoser)
        }
    }

    fun getSelectedLoser(): PlayerModel? {
        return if (selectedLoser != RecyclerView.NO_POSITION) {
            items[selectedLoser]
        } else null
    }
}