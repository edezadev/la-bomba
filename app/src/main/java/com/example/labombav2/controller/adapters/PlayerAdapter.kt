package com.example.labombav2.controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
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
        val popUp: ListPopupWindow = setPopUp(holder.tvPlayer.context, holder)

        holder.tvPlayer.text = item.name

        holder.tvPlayer.setOnLongClickListener{
            popUp.show()
            true
        }
    }

    private fun setPopUp(context: Context, holder: PlayerAdapter.ViewHolder): ListPopupWindow {
        val popUp = ListPopupWindow(
            context,
            null,
            com.google.android.material.R.attr.listPopupWindowStyle)

//      Establecer textView como ancla del popup
        popUp.anchorView = holder.tvPlayer

//      Lista de contenido
        val items = listOf(context.getString(R.string.edit), context.getString(R.string.delete))
        val adapter = ArrayAdapter(holder.tvPlayer.context, R.layout.item_popup_menu, items)
        popUp.setAdapter(adapter)

        popUp.setOnItemClickListener { _, _, position, _ ->
            when(position) {
                0 -> Toast.makeText(context, "Edit", Toast.LENGTH_LONG).show()
                1 -> Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG).show()
            }
            popUp.dismiss()
        }

        return popUp
    }
}