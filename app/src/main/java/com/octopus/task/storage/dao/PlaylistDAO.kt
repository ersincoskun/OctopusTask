package com.octopus.task.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.octopus.task.model.DataItem

@Dao
interface PlaylistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: List<DataItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSinglePlaylist(playlist: DataItem)

    @Query("SELECT * FROM playlistdb")
    suspend fun getPlaylist(): List<DataItem>

    @Query("DELETE FROM playlistdb")
    suspend fun deletePlaylist()
}