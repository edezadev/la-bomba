package com.edeza.labomba.utils.listeners

import com.edeza.labomba.models.TopicModel

interface OnTopicInsertedListener {
    fun onTopicInserted(newTopic: TopicModel)
}