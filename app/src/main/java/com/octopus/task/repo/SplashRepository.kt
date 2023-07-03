package com.octopus.task.repo

import com.octopus.task.model.PlaylistModel
import com.octopus.task.model.SpecifyBodyModel
import com.octopus.task.utils.Resource


interface SplashRepository {
    suspend fun getPlaylistFromDb(): List<PlaylistModel>
    suspend fun specify(specifyBodyModel: SpecifyBodyModel):Resource
    suspend fun getPlaylistFromApi(): Resource
    suspend fun deletePlaylistFromDB()
    suspend fun savePlaylistToDb(playlist: List<PlaylistModel>)
}