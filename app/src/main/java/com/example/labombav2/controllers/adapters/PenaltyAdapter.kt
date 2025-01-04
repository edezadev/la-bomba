package com.example.labombav2.controllers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.ItemPenaltyBinding
import com.example.labombav2.models.PenaltyModel
import com.example.labombav2.utils.Constants
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.PenaltyDbManager
import com.example.labombav2.utils.GameSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PenaltyAdapter(private var items: MutableList<PenaltyModel>) :
    RecyclerView.Adapter<PenaltyAdapter.ViewHolder>() {

    private var selectedPenaltyId: String? = null

    inner class ViewHolder(binding: ItemPenaltyBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbPenalty = binding.cbPenalty
        var uid: String? = null
        lateinit var penalty: PenaltyModel //Referencia al objeto(item) actual

        init {
//          Obtener UID de usuario para eliminar el penalty
            FirebaseAuthManager.getUid {
                uid = it
            }
//          Establecer una vez el long click
            cbPenalty.setOnLongClickListener {
                if (::penalty.isInitialized) {
                    showDeleteDialog(this, penalty)
                }
                true//Obligatorio para longClick, indica que se manejó el evento
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPenaltyBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.penalty = item //almacenamos el objeto Penalty
        holder.cbPenalty.text = item.name

        holder.cbPenalty.isChecked = (item.id == selectedPenaltyId)

        holder.cbPenalty.setOnClickListener {
            if (item.id == selectedPenaltyId) {
                selectedPenaltyId = null
            } else {
                selectedPenaltyId = item.id

                notifyDataSetChanged()
            }
        }
    }

    private fun showDeleteDialog(holder: ViewHolder, item: PenaltyModel) {
        MaterialAlertDialogBuilder(holder.cbPenalty.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_penalty)
            .setPositiveButton(R.string.action_positive) { dialog, _ ->
//              Eliminar de la BD
                holder.uid?.let { PenaltyDbManager.deletePenalty(it, item.id) }
                notifyItemRemoved(itemCount)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}