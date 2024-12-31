package com.example.labombav2.controllers.dialogs

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.labombav2.R
import com.example.labombav2.databinding.BottomSheetAddPlayerBinding
import com.example.labombav2.models.PlayerModel
import com.example.labombav2.utils.Constants
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.utils.listeners.OnPlayerInsertedListener
import com.example.labombav2.config.database.PlayerDbManager
import com.example.labombav2.utils.GameSession
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
    private lateinit var btnEditPlayer: MaterialButton
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
            btnEditPlayer = it.btnEditPlayer
        }
//      limitar caracteres
        etPlayer.filters = arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_player)))

        val mBehavior = BottomSheetBehavior.from(bshAddPlayer)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

//      Recibir el id del jugador y cambiar la acción de AGREGAR a EDITAR
        val idPlayer = arguments?.getString(Constants.ID_PLAYER)
        idPlayer?.let { changeActionButton(it) }

        btnAddPlayer.setOnClickListener { addPlayer() }
        btnEditPlayer.setOnClickListener { editPlayer(idPlayer) }

        return binding?.root
    }

    private fun changeActionButton(idPlayer: String) {
        btnAddPlayer.visibility = View.GONE
        btnEditPlayer.visibility = View.VISIBLE
        getNamePlayer(idPlayer)
    }

    private fun getNamePlayer(idPlayer: String) {
        FirebaseAuthManager.getUid { uid ->
            PlayerDbManager.getPlayer(uid, idPlayer) { player ->
                etPlayer.setText(player.name)
            }
        }
    }

    private fun addPlayer() {
        val namePlayer = etPlayer.text?.toString()?.trim()

        if (textValidation(namePlayer)) {
            if (namePlayer!=null) {
                insertedListener?.onPlayerInserted(PlayerModel(name = namePlayer))
                dismiss()
            }
        }
    }

    private fun editPlayer(idPlayer: String?) {
        val namePlayer = etPlayer.text?.toString()?.trim()

        if (textValidation(namePlayer)) {
            FirebaseAuthManager.getUid { uid ->
                if (namePlayer != null) {
                    PlayerDbManager.updatePlayer(uid,
                        idPlayer!!,
                        mapOf(Constants.NAME to namePlayer))
                    //Editar si existe
                    GameSession.players.find { it.id == idPlayer }?.let {
                        it.name = namePlayer
                    }
                    dismiss()
                }
            }
        }
    }

    private fun textValidation(namePlayer: String?): Boolean {
        if (namePlayer.isNullOrEmpty()) {
            setError(getString(R.string.error_empty_player))
            return false
        }

        if(namePlayer.length < 5) {
            setError(getString(R.string.error_length_short))
            return false
        }
        return true
    }

    private fun setError(text: String) {
        tilPlayer.error = text
        tilPlayer.requestFocus()
    }

    //  Metodo de configuración para el linster
    fun setOnDataInsertedListener(listener: OnPlayerInsertedListener) {
        this.insertedListener = listener
    }

    companion object {
        const val TAG = "AddPlayerModalBottomSheet"
    }
}