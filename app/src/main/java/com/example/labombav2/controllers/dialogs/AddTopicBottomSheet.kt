package com.example.labombav2.controllers.dialogs

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.labombav2.R
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.config.database.TopicDbManager
import com.example.labombav2.databinding.BottomSheetAddTopicBinding
import com.example.labombav2.models.TopicModel
import com.example.labombav2.utils.Constants
import com.example.labombav2.utils.listeners.OnTopicInsertedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddTopicBottomSheet : BottomSheetDialogFragment() {
    private var binding: BottomSheetAddTopicBinding? = null

    private lateinit var bshAddTopic: LinearLayout
    private lateinit var tilTopic: TextInputLayout
    private lateinit var etTopic: TextInputEditText
    private lateinit var btnAddTopic: MaterialButton
    private lateinit var btnEditTopic: MaterialButton
    private var insertedListener: OnTopicInsertedListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddTopicBinding.inflate(inflater, container, false)
        binding?.let {
            bshAddTopic = it.bshAddTopic
            tilTopic = it.tilTopic
            etTopic = it.etTopic
            btnAddTopic = it.btnAddTopic
            btnEditTopic = it.btnEditTopic
        }
//      limitar caracteres
        etTopic.filters = arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_topic)))

//      Configurar el behavior
        val mBehavior = BottomSheetBehavior.from(bshAddTopic)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

//      Recibir el id del Tema y cambiar la acción de AGREGAR o EDITAR
        val idTopic = arguments?.getString(Constants.ID_TOPIC)
        idTopic?.let { changeActionButton(it) }

        btnAddTopic.setOnClickListener { addTopic() }
        btnEditTopic.setOnClickListener { editTopic(idTopic) }

        return binding?.root
    }

    private fun changeActionButton(idTopic: String) {
        btnAddTopic.visibility = View.GONE
        btnEditTopic.visibility = View.VISIBLE
        getNameTopic(idTopic)
    }

    private fun getNameTopic(idTopic: String) {
        FirebaseAuthManager.getUid { uid ->
            TopicDbManager.getTopic(uid, idTopic) { topic ->
                etTopic.setText(topic.name)
            }
        }
    }

    private fun addTopic() {
        val nameTopic = etTopic.text?.toString()?.trim()

        if (textValidation(nameTopic)) {
            if (nameTopic != null) {
                insertedListener?.onTopicInserted(TopicModel(name = nameTopic))
                dismiss()
            }
        }
    }

    private fun editTopic(idTopic: String?) {
        val nameTopic = etTopic.text?.toString()?.trim()

        if (textValidation(nameTopic)) {
            FirebaseAuthManager.getUid { uid ->
                if (nameTopic != null) {
                    TopicDbManager.updateTopic(uid,
                        idTopic!!,
                        mapOf(Constants.NAME to nameTopic))
                    dismiss()
                }
            }
        }
    }

    private fun textValidation(nameTopic: String?): Boolean {
        if (nameTopic.isNullOrEmpty()) {
            setError(getString(R.string.error_empty_topic))
            return false
        }

        if (nameTopic.length < 8) {
            setError(getString(R.string.error_length_short))
            return false
        }
        return true
    }

    private fun setError(text: String) {
        tilTopic.error = text
        tilTopic.requestFocus()
    }

    //  Métodoo de configuración para asignar un listener específico que implementa la interfaz
    fun setOnDataInsertedListener(listener: OnTopicInsertedListener) {
        this.insertedListener = listener
    }

    companion object {
        const val TAG = "AddTopicModalBottomSheet"
    }
}