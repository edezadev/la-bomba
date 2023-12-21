package com.example.labombav2.model

data class PenaltyModel (
//  Se les da un valor por defecto (como constructor implícito) para la deserialización de Firestore
    val name: String = "",
    var isChecked: Boolean = false
)
