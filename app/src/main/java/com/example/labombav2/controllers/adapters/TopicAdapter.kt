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
import com.example.labombav2.utils.GameSession
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
        var uid: String? = null
        private val popUp: ListPopupWindow = createListPopupWindow(itemView.context)
        lateinit var topic: TopicModel //Referencia al objeto(item) actual

        init {
//          Obtener UID de usuario para usarlo al eliminar el tema
            FirebaseAuthManager.getUid {
                uid = it
            }
//          Establecer una vez el long Click
            cbTopic.setOnLongClickListener {
                if (::topic.isInitialized) {  // Solo mostramos si hay un topic cargado
                    popUp.show()
                }
                true
            }
        }

        private fun createListPopupWindow(context: Context): ListPopupWindow {
            val popUp = ListPopupWindow(
                context,
                null,
                com.google.android.material.R.attr.listPopupWindowStyle
            )

//          Establecer textView como ancla del popup
            popUp.anchorView = cbTopic

//          Configuración del contenido de la lista y el listener
            val items = listOf(context.getString(R.string.edit), context.getString(R.string.delete))
            val adapter = ArrayAdapter(context, R.layout.item_popup_menu, items)
            popUp.setAdapter(adapter)
            popUp.setOnItemClickListener { _, _, index, _ ->
                when (index) {
                    0 -> showEditTopic(topic.id)
                    1 -> showDeleteDialog(this, topic)
                }
                popUp.dismiss()
            }

            return popUp
        }
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
        holder.topic = item //Almacenamos el objeto TopicModel
        holder.cbTopic.text = item.name

        /*Verificar si GameSession contiene el item actual, si lo contiene modificar a checked*/
        holder.cbTopic.isChecked = GameSession.topics.contains(item)

        /*Escuchar cuando se marca/desmarca el item, se agregue/elimine de GameSession*/
        holder.cbTopic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                GameSession.topics.add(item)
            } else {
                GameSession.topics.remove(item)
            }
        }
    }

    private fun showEditTopic(id: String) {
        val bottomSheet = AddTopicBottomSheet()
        bottomSheet.arguments = bundleOf(Constants.ID_TOPIC to id)
        bottomSheet.show(fragmentManager.beginTransaction(), AddTopicBottomSheet.TAG)
    }

    private fun showDeleteDialog(holder: ViewHolder, item: TopicModel) {
        val position = items.indexOf(item)
        MaterialAlertDialogBuilder(holder.cbTopic.context)
            .setTitle(R.string.title_alert)
            .setMessage(R.string.message_delete_topic)
            .setPositiveButton(R.string.action_positive) { dialog, _ ->
                holder.uid?.let { TopicDbManager.deleteTopic(it, item.id) }
                GameSession.topics.remove(item) //eliminar en caso que se elimine de la DB
                if (position != -1){
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
/*TODO: continuar con el castigo y finalizar con la impresion por consola de todos los
* datos guardados para el juego*/