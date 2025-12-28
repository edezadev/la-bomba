package com.example.labombav2.controllers.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.controllers.adapters.LoserAdapter
import com.example.labombav2.databinding.BottomSheetSelectLoserBinding
import com.example.labombav2.utils.GameSession
import com.example.labombav2.utils.listeners.OnLoserListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SelectLoserBottomSheet : BottomSheetDialogFragment() {
    private var binding: BottomSheetSelectLoserBinding? = null
    private var adapter: LoserAdapter? = null
    private lateinit var bshSelectLoser: LinearLayout
    private lateinit var recyclerSelectLoser: RecyclerView
    private lateinit var btnConfirmLoser: MaterialButton
    private var loserListener: OnLoserListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

//        Evitar que se cierre al tocar fuera del BottomSheet
        dialog.setCanceledOnTouchOutside(false)

//        Se ejecuta cuando el dialogo ya está mostrando su vista
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let{
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//              Permite que el BottomSheet se muestre a la mitad antes de expandirse por completo
                behavior.skipCollapsed = false
                behavior.isHideable = false //Evita que pase a STATE_HIDDEN
            }
        }
//        No permite la función de BACK para cerrar el BottomSheet
        isCancelable = false
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSelectLoserBinding.inflate(inflater, container, false)
        binding?.let {
            bshSelectLoser = it.bshSelectLoser
            recyclerSelectLoser = it.recyclerSelectLoser
            btnConfirmLoser = it.btnConfirmLoser
        }

        setupRecyclerView()
        btnConfirmLoser.setOnClickListener { confirmLoser() }

        return binding?.root
    }

    private fun setupRecyclerView() {
        adapter = LoserAdapter(GameSession.players)
        recyclerSelectLoser.layoutManager = LinearLayoutManager(requireContext())
        recyclerSelectLoser.adapter = adapter
    }

    private fun confirmLoser() {
        val selected = adapter?.getSelectedLoser()
        if (selected != null) {
            val losers = GameSession.loser
            val index = losers.indexOfFirst{ it.player == selected }

    //      Actualizar los puntos del perdedor seleccionado
            if (index != -1) losers [index] = losers[index].copy(points = losers[index].points + 1)

//            Notifica al listener antes de cerrar el dialogo
            loserListener?.onBottomSheetDismissed()
            dismiss()
        } else {
            Toast.makeText(requireContext(),
                "Selecciona el perdedor de la ronda", Toast.LENGTH_LONG).show()
        }
    }

//    Setter para registrar la interfaz
    fun setOnLoserListener(listener: OnLoserListener) {
        this.loserListener = listener
    }

    companion object{
        const val TAG = "SelectLoserModalBottomSheet"
    }
}