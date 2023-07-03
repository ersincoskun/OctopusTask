package com.octopus.task.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlistdb")
data class PlaylistModel(
    val id: String,
    val mediaList: List<String>,
    @PrimaryKey(autoGenerate = true)
    var primaryId: Int? = null
)
