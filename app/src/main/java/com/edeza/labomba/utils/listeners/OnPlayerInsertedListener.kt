package com.edeza.labomba.utils.listeners

import com.edeza.labomba.models.PlayerModel

interface OnPlayerInsertedListener {
    fun onPlayerInserted(newPlayer: PlayerModel)
}