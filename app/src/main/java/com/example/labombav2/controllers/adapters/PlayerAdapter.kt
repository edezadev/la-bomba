package com.example.labombav2.controllers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.controllers.dialogs.AddPlayerBottomSheet
import com.example.labombav2.databinding.ItemPlayerBinding
import com.example.labombav2.models.PlayerModel
import com.example.labombav2.utils.Constants
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.PlayerDbManager
import com.example.labombav2.utils.GameSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerAdapter(
    private var items: MutableList<PlayerModel>,
    private var fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemPlayerBinding):
        RecyclerView.ViewHolder(binding.root) {
        val tvPlayer = binding.tvPlayer
        var uid: String? = null
        private val popUp: ListPopupWindow = createListPopupWindow(itemView.context)
        lateinit var player: PlayerModel //Referencia la objeto(item) actual

        init {
            FirebaseAuthManager.getUid {
                uid = it
            }
//          Establecer una vez el long click
            tvPlayer.setOnLongClickListener{
                if (::player.isInitialized) { //solo mostramos si hay un player cargado
                    popUp.show()
                }
                    true
            }
        }
        private fun createListPopupWindow(context: Context): ListPopupWindow {
            val popUp = ListPopupWindow(
                context,
                null,
                com.google.android.material.R.attr.listPopupWindowStyle)

//          Establecer textView como ancla del popup
            popUp.anchorView = tvPlayer

//          Lista de contenido
            val items = listOf(context.getString(R.string.edit), context.getString(R.string.delete))
            val adapter = ArrayAdapter(context, R.layout.item_popup_menu, items)
            popUp.setAdapter(adapter)

            popUp.setOnItemClickListener { _, _, index, _ ->
                when(index) {
                    0 -> showEditPlayer(player.id)
                    1 -> showDeleteDialog(this, player)
                }
                popUp.dismiss()
            }

            return popUp
        }
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
        holder.player = item //Almacenamos el objeto Payer

        holder.tvPlayer.text = item.name
    }

    private fun showEditPlayer(id: String) {
        val bottomSheet = AddPlayerBottomSheet()
        bottomSheet.arguments = bundleOf(Constants.ID_PLAYER to id)
        bottomSheet.show(fragmentManager.beginTransaction(), AddPlayerBottomSheet.TAG)
    }

    private fun showDeleteDialog(holder: ViewHolder, item: PlayerModel) {
        val position = items.indexOf(item)
        MaterialAlertDialogBuilder(holder.tvPlayer.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_player)
            .setPositiveButton(R.string.action_positive) {dialog, _ ->
                holder.uid?.let { PlayerDbManager.deletePlayer(it, item.id) }
                GameSession.players.remove(item) //Eliminar cuando se elimine en la BD
                if (position != -1) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
                notifyItemRemoved(itemCount)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) {dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}