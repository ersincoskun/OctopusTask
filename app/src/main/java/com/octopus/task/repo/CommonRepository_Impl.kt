package com.octopus.task.repo

import android.content.Context
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.model.SpecifyBodyModel
import com.octopus.task.remote.ApiInterface
import com.octopus.task.storage.dao.PlaylistDAO
import com.octopus.task.utils.Resource
import com.octopus.task.utils.printErrorLog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommonRepository_Impl @Inject constructor(
    @ApplicationContext val context: Context,
    val retrofitApi: ApiInterface,
    val playlistDao: PlaylistDAO,
    val preferencesHelper: PreferencesHelper,
) : CommonRepository {
    override suspend fun getPlaylistFromServer(): Resource {
        return try {
            val response = retrofitApi.getPlaylist(preferencesHelper.deviceId)
            printErrorLog("register response: $response")
            response?.let { safeResponse ->
                if (safeResponse.isSuccessful) {
                    safeResponse.body()?.let { responseBody ->
                        printErrorLog("register response body: $responseBody")
                        return Resource.Success(responseBody)
                    } ?: kotlin.run {
                        printErrorLog("null body")
                        return@run Resource.Error("empty body")
                    }
                } else {
                    printErrorLog("response not successful")
                    return@let Resource.Error("response not successful")
                }
            } ?: kotlin.run {
                printErrorLog("null response")
                return@run Resource.Empty
            }
        } catch (e: Exception) {
            printErrorLog("request catch $e")
            return Resource.Error("catch")
        }
    }

    override suspend fun specify(specifyBodyModel: SpecifyBodyModel): Resource {
        return try {
            val response = retrofitApi.specify(preferencesHelper.deviceId,specifyBodyModel)
            printErrorLog("register response: $response")
            response?.let { safeResponse ->
                if (safeResponse.isSuccessful) {
                    safeResponse.body()?.let { responseBody ->
                        printErrorLog("register response body: $responseBody")
                        return Resource.Success(responseBody)
                    } ?: kotlin.run {
                        printErrorLog("null body")
                        return@run Resource.Error("empty body")
                    }
                } else {
                    printErrorLog("response not successful")
                    return@let Resource.Error("response not successful")
                }
            } ?: kotlin.run {
                printErrorLog("null response")
                return@run Resource.Error("empty response")
            }
        } catch (e: Exception) {
            printErrorLog("request catch $e")
            return Resource.Error("catch")
        }
    }

    override suspend fun getPlaylistFromDb(): List<DataItem> {
        return playlistDao.getPlaylist()
    }

    override suspend fun insertPlaylistToDb(playlist: List<DataItem>) {
        playlistDao.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylistFromDB() {
        playlistDao.deletePlaylist()
    }
}
