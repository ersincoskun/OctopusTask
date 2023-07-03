package com.octopus.task.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.octopus.task.model.PlaylistModel

@Dao
interface PlaylistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: List<PlaylistModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSinglePlaylist(playlist: PlaylistModel)

    @Query("SELECT * FROM playlistdb")
    suspend fun getPlaylist(): List<PlaylistModel>

    @Query("DELETE FROM playlistdb")
    suspend fun deletePlaylist()
}