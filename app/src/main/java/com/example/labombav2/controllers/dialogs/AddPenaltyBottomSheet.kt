package com.example.labombav2.controllers.dialogs

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.labombav2.R
import com.example.labombav2.databinding.BottomSheetAddPenaltyBinding
import com.example.labombav2.models.PenaltyModel
import com.example.labombav2.utils.listeners.OnPenaltyInsertedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddPenaltyBottomSheet: BottomSheetDialogFragment() {
    private var binding: BottomSheetAddPenaltyBinding? = null

    private lateinit var bshAddPenalty: LinearLayout
    private lateinit var tilPenalty: TextInputLayout
    private lateinit var etPenalty: TextInputEditText
    private lateinit var btnAddPenalty: MaterialButton
    private var insertedListener: OnPenaltyInsertedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddPenaltyBinding.inflate(inflater, container, false)
        binding?.let {
            bshAddPenalty = it.bshAddPenalty
            tilPenalty = it.tilPenalty
            etPenalty = it.etPenalty
            btnAddPenalty = it.btnAddPenalty
        }
//      limitar caracteres
        etPenalty.filters = arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_penalty)))

        val mBehavior = BottomSheetBehavior.from(bshAddPenalty)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnAddPenalty.setOnClickListener {
            addPenalty()
        }

        return binding?.root
    }

//  Métodoo de configuración para asignar un listener específico que implementa la interfaz
    fun setOnDataInsertedListener ( listener: OnPenaltyInsertedListener) {
        this.insertedListener = listener
    }

    private fun addPenalty() {
        val namePenalty = etPenalty.text?.toString()?.trim()
        if (namePenalty.isNullOrEmpty()) {
            setError(getString(R.string.error_empty_penalty))
            return
        }

        if (namePenalty.length < 5) {
            setError(getString(R.string.error_length_short))
            return
        }

        insertedListener?.onPenaltyInserted(PenaltyModel(name = namePenalty))
        dismiss()
    }

    private fun setError(text: String) {
        tilPenalty.error = text
        tilPenalty.requestFocus()
    }

    companion object {
        const val TAG = "AddPenaltyModalBottomSheet"
    }
}