package com.example.labombav2.models

data class PenaltyModel (
//  Se les da un valor por defecto (como constructor implícito) para la deserialización de Firestore
    var id: String = "",
    val name: String = "",
//  Para que se pueda guardar en la base de datos con "is" se agrega la notación
    @field:JvmField
    var isChecked: Boolean = false
)
