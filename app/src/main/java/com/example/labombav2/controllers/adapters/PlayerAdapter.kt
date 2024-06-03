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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerAdapter(
    private var items: MutableList<PlayerModel>,
    private var fragmentManager: FragmentManager
) :
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
        val popUp: ListPopupWindow = setPopUp(holder.tvPlayer.context, holder, item.id)
        FirebaseAuthManager.getUid {
            holder.uid = it
        }

        holder.tvPlayer.text = item.name

        holder.tvPlayer.setOnLongClickListener{
            popUp.show()
            true
        }
    }

    private fun setPopUp(context: Context, holder: ViewHolder, id: String): ListPopupWindow {
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

        popUp.setOnItemClickListener { _, _, index, _ ->
            when(index) {
                0 -> showEditPlayer(id)
                1 -> showDeleteDialog(holder, id)
            }
            popUp.dismiss()
        }

        return popUp
    }

    private fun showEditPlayer(id: String) {
        val bottomSheet = AddPlayerBottomSheet()
        bottomSheet.arguments = bundleOf(Constants.ID_PLAYER to id)
        bottomSheet.show(fragmentManager.beginTransaction(), AddPlayerBottomSheet.TAG)
    }

    private fun showDeleteDialog(holder: ViewHolder, id: String) {
        MaterialAlertDialogBuilder(holder.tvPlayer.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_player)
            .setPositiveButton(R.string.action_positive) {dialog, _ ->
                PlayerDbManager.deletePlayer(holder.uid, id)
                notifyItemRemoved(itemCount)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) {dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}