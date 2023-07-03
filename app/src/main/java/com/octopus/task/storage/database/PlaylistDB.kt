package com.octopus.task.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.octopus.task.storage.dao.PlaylistDAO

@Database(entities = [PlaylistModel::class], version = 1)
abstract class PlaylistDB : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDAO
}
