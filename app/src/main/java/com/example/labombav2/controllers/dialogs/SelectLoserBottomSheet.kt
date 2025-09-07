package com.example.labombav2.controllers.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.controllers.adapters.LoserAdapter
import com.example.labombav2.databinding.BottomSheetSelectLoserBinding
import com.example.labombav2.utils.GameSession
import com.example.labombav2.utils.listeners.OnLoserListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectLoserBottomSheet : BottomSheetDialogFragment() {
    private var binding: BottomSheetSelectLoserBinding? = null
    private var adapter: LoserAdapter? = null
    private lateinit var bshSelectLoser: LinearLayout
    private lateinit var recyclerSelectLoser: RecyclerView
    private var loserListener: OnLoserListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSelectLoserBinding.inflate(inflater, container, false)
        binding?.let {
            bshSelectLoser = it.bshSelectLoser
            recyclerSelectLoser = it.recyclerSelectLoser
        }

        setupRecyclerView()
        return binding?.root
    }

    private fun setupRecyclerView() {
        adapter = LoserAdapter(GameSession.players, parentFragmentManager)
        recyclerSelectLoser.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerSelectLoser.adapter = adapter
    }

    /*
    * Cuando usas un BottomSheetDialogFragment, no necesitas aplicar manualmente un
    * layout_behavior sobre tu LinearLayout (bshSelectLoser en el XML). Eso rompe el gesto: estás
    * tratando de forzar un behavior dentro de un layout que ya está siendo envuelto automáticamente
    * por el BottomSheetDialog. Lo correcto es usar el behavior del propio dialog en el onStart
    * */
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    companion object{
        const val TAG = "SelectLoserModalBottomSheet"
    }
}