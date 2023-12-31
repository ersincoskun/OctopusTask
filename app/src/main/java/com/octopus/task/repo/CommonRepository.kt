package com.octopus.task.repo

import com.octopus.task.model.DataItem
import com.octopus.task.model.SpecifyBodyModel
import com.octopus.task.utils.Resource


interface CommonRepository {
    suspend fun getPlaylistFromDb(): List<DataItem>
    suspend fun specify(specifyBodyModel: SpecifyBodyModel):Resource
    suspend fun getPlaylistFromServer(): Resource
    suspend fun deletePlaylistFromDB()
    suspend fun insertPlaylistToDb(playlist: List<DataItem>)
}