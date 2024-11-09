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
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.TopicDbManager
import com.example.labombav2.controllers.dialogs.AddTopicBottomSheet
import com.example.labombav2.databinding.ItemTopicBinding
import com.example.labombav2.models.TopicModel
import com.example.labombav2.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Recibir una página con la lista de temas, y crear/mostrar los items para cada tema
 **/
class TopicAdapter(
    private var items: MutableList<TopicModel>,
    private var fragmentManager: FragmentManager
):
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemTopicBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cbTopic = binding.root
        lateinit var uid: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val popUp: ListPopupWindow = setPopUp(holder.cbTopic.context, holder, item.id)
//      Obtener UID de usuario para usarlo al eliminar el tema
        FirebaseAuthManager.getUid {
            holder.uid = it
        }

        holder.cbTopic.text = item.name

        holder.cbTopic.setOnLongClickListener {
            popUp.show()
            true
        }
    }

    private fun setPopUp(context: Context, holder: ViewHolder, id: String): ListPopupWindow {
        val popUp = ListPopupWindow(
            context,
            null,
            com.google.android.material.R.attr.listPopupWindowStyle
        )

//      Establecer textView como ancla del popup
        popUp.anchorView = holder.cbTopic

//      Lista de contenido
        val items = listOf(context.getString(R.string.edit), context.getString(R.string.delete))
        val adapter = ArrayAdapter(context, R.layout.item_popup_menu, items)
        popUp.setAdapter(adapter)
        popUp.setOnItemClickListener { _, _, index, _ ->
            when (index) {
                0 -> showEditTopic(id)
                1 -> showDeleteDialog(holder, id)
            }
            popUp.dismiss()
        }

        return popUp
    }

    private fun showEditTopic(id: String) {
        val bottomSheet = AddTopicBottomSheet()
        bottomSheet.arguments = bundleOf(Constants.ID_TOPIC to id)
        bottomSheet.show(fragmentManager.beginTransaction(), AddTopicBottomSheet.TAG)
    }

    private fun showDeleteDialog(holder: ViewHolder, id: String) {
        MaterialAlertDialogBuilder(holder.cbTopic.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_topic)
            .setPositiveButton(R.string.action_positive) { dialog, _ ->
                TopicDbManager.deleteTopic(holder.uid, id)
                notifyItemRemoved(itemCount)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}