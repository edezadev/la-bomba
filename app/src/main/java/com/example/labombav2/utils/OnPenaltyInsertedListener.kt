package com.example.labombav2.utils

import com.example.labombav2.model.PenaltyModel

interface OnPenaltyInsertedListener {
    fun onPenaltyInserted(newPenalty: PenaltyModel)
}