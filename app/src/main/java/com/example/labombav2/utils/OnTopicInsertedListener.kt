package com.example.labombav2.utils

import com.example.labombav2.model.TopicModel

interface OnTopicInsertedListener {
    fun onTopicInserted(newTopic: TopicModel)
}