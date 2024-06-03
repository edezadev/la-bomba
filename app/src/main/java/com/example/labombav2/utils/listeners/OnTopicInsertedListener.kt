package com.example.labombav2.utils.listeners

import com.example.labombav2.models.TopicModel

interface OnTopicInsertedListener {
    fun onTopicInserted(newTopic: TopicModel)
}