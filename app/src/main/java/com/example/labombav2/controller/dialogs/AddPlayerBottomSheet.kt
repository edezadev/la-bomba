package com.example.labombav2.controller.dialogs

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.labombav2.R
import com.example.labombav2.databinding.BottomSheetAddPlayerBinding
import com.example.labombav2.model.PlayerModel
import com.example.labombav2.utils.OnPlayerInsertedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddPlayerBottomSheet : BottomSheetDialogFragment() {
    private var binding: BottomSheetAddPlayerBinding? = null
    private lateinit var bshAddPlayer: LinearLayout
    private lateinit var tilPlayer: TextInputLayout
    private lateinit var etPlayer: TextInputEditText
    private lateinit var btnAddPlayer: MaterialButton
    private var insertedListener: OnPlayerInsertedListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddPlayerBinding.inflate(inflater, container, false)
        binding?.let {
            bshAddPlayer = it.bshAddPlayer
            tilPlayer = it.tilPlayer
            etPlayer = it.etPlayer
            btnAddPlayer = it.btnAddPlayer
        }
//      limitar caracteres
        etPlayer.filters = arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_player)))

        val mBehavior = BottomSheetBehavior.from(bshAddPlayer)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnAddPlayer.setOnClickListener { addPlayer() }

        return binding?.root
    }

//  Metodo de configuración para el linster
    fun setOnDataInsertedListener(listener: OnPlayerInsertedListener) {
        this.insertedListener = listener
    }

    private fun addPlayer() {
        val namePlayer = etPlayer.text?.toString()?.trim()
        if (namePlayer.isNullOrEmpty()) {
            setError(getString(R.string.error_empty_player))
            return
        }

        if(namePlayer.length < 5) {
            setError(getString(R.string.error_length_short))
            return
        }

        insertedListener?.onPlayerInserted(PlayerModel(name = namePlayer))
        dismiss()
    }

    private fun setError(text: String) {
        tilPlayer.error = text
        tilPlayer.requestFocus()
    }

    companion object {
        const val TAG = "AddPlayerModalBottomSheet"
    }
}