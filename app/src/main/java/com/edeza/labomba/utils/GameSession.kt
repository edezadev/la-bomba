package com.edeza.labomba.utils

import com.edeza.labomba.models.LoserModel
import com.edeza.labomba.models.PenaltyModel
import com.edeza.labomba.models.PlayerModel
import com.edeza.labomba.models.TopicModel

object GameSession {
    var penalty: PenaltyModel? = null
    var players: MutableList<PlayerModel> = mutableListOf()
    var topics: MutableList<TopicModel> = mutableListOf()
    var time: Int? = 30000
    var loser: MutableList<LoserModel> = mutableListOf()

    fun reset() {
        penalty = null
        players.clear()
        topics.clear()
        time = 30000
        loser.clear()
    }

    fun hasChanges(): Boolean {
        return penalty != null || topics.isNotEmpty()
    }
}