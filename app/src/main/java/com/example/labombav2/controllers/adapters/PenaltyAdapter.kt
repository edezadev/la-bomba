package com.example.labombav2.controllers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.databinding.ItemPenaltyBinding
import com.example.labombav2.models.PenaltyModel
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.PenaltyDbManager
import com.example.labombav2.utils.GameSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PenaltyAdapter(private var items: MutableList<PenaltyModel>) :
    RecyclerView.Adapter<PenaltyAdapter.ViewHolder>() {
    var uid: String? = null
    init {
//      Obtener UID una sola vez, para eliminar el penalty
        FirebaseAuthManager.getUid {
            uid = it
        }
    }

    inner class ViewHolder(binding: ItemPenaltyBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbPenalty = binding.cbPenalty
        lateinit var penalty: PenaltyModel //Referencia al objeto(item) actual

        init {
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

    override fun getItemCount(): Int =  items.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.penalty = item //almacenamos el objeto Penalty
        holder.cbPenalty.text = item.name

        holder.cbPenalty.isChecked = (item == GameSession.penalty)

        holder.cbPenalty.setOnClickListener {
            if (item == GameSession.penalty) {
                GameSession.penalty = null
            } else {
                GameSession.penalty = item

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
                uid?.let {
                    PenaltyDbManager.deletePenalty(it, item.id)
                    GameSession.penalty = null
                    val position = items.indexOf(item)
                    if (position != -1) {
                        items.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}