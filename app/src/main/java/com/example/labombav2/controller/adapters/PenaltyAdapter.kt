package com.example.labombav2.controller.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.ItemPenaltyBinding
import com.example.labombav2.model.PenaltyModel
import com.example.labombav2.utils.Constants
import com.example.labombav2.utils.FirebaseAuthManager
import com.example.labombav2.utils.PenaltyDbManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PenaltyAdapter(private var items: MutableList<PenaltyModel>) :
    RecyclerView.Adapter<PenaltyAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPenaltyBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbPenalty = binding.cbPenalty
        lateinit var uid: String
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
        FirebaseAuthManager.getUid {
            holder.uid = it
        }

        holder.cbPenalty.setOnClickListener {
            val checked = (it as CheckBox).isChecked //guardamos el estado actual al hacer click
            updateChecked(holder, position, checked)

            //cambiar a true solo el seleccionado
            item.isChecked = holder.cbPenalty.isChecked
        }

        holder.cbPenalty.setOnLongClickListener {
            showDeleteDialog(holder, item)
            true//Obligatorio para longClick, indica que se manejó el evento
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateChecked(holder: ViewHolder, position: Int, checked: Boolean) {
    /* items.indices: proporciona un rango de índices de la lista, y poder realizar operaciones
    * basadas en esos índices */
        for (index in items.indices) {
            if (index != position) {
//              cambiar a false el estado del item que es distinto al que fue accionado
//                items[index].isChecked = false
                PenaltyDbManager.updatePenalty(
                    holder.uid,
                    items[index].id,
                    mapOf(Constants.IS_CHECKED to false)
                )
            }else{
                /* Se asigna "checked" y no solo "true" porque contiene el valor de cuando se
                 * acciona el mismo checkbox */
                PenaltyDbManager.updatePenalty(
                    holder.uid,
                    items[index].id,
                    mapOf(Constants.IS_CHECKED to checked)
                )
            }
        }
        notifyDataSetChanged()
    }

    private fun showDeleteDialog(holder: ViewHolder, item: PenaltyModel) {
        MaterialAlertDialogBuilder(holder.cbPenalty.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_penalty)
            .setPositiveButton(R.string.action_positive) { dialog, _ ->
//              Eliminar de la BD
                PenaltyDbManager.deletePenalty(holder.uid, item.id)
                notifyItemRemoved(itemCount)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}