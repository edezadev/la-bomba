package com.edeza.labomba.utils.listeners

import com.edeza.labomba.models.PenaltyModel

interface OnPenaltyInsertedListener {
    fun onPenaltyInserted(newPenalty: PenaltyModel)
}