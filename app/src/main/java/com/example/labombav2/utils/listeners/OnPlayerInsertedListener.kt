package com.example.labombav2.utils.listeners

import com.example.labombav2.models.PlayerModel

interface OnPlayerInsertedListener {
    fun onPlayerInserted(newPlayer: PlayerModel)
}