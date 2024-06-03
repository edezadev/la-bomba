package com.example.labombav2.utils.listeners

import com.example.labombav2.models.PenaltyModel

interface OnPenaltyInsertedListener {
    fun onPenaltyInserted(newPenalty: PenaltyModel)
}