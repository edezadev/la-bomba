package com.example.labombav2.utils

import com.example.labombav2.model.PlayerModel

interface OnPlayerInsertedListener {
    fun onPlayerInserted(newPlayer: PlayerModel)
}