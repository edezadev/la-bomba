package com.example.labombav2.utils

import com.example.labombav2.models.PlayerModel
import com.example.labombav2.models.TopicModel

object GameSession {
    var penalty: String? = null
    var players: MutableList<PlayerModel> = mutableListOf()
    var topics: MutableList<TopicModel> = mutableListOf()
    var time: Int? = 30000

    fun reset() {
        penalty = null
        players.clear()
        topics.clear()
        time = null
    }
}