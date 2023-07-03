package com.octopus.task.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.octopus.task.manager.DownloadManager
import com.octopus.task.model.DataItem
import com.octopus.task.model.ResponseModel
import com.octopus.task.repo.SplashRepository
import com.octopus.task.storage.dao.PlaylistDAO
import com.octopus.task.utils.Resource
import com.octopus.task.utils.printErrorLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@HiltWorker
class GetDataFromApiWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val splashRepository: SplashRepository,
    private val playlistDao: PlaylistDAO,
    private val downloadManager: DownloadManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        printErrorLog("worker ran")
        getPlayList()
        return Result.success()
    }

    private suspend fun getPlayList() {
        val resource = splashRepository.getPlaylistFromApi()
        if (resource is Resource.Success<*>) {
            val response = resource.data as ResponseModel
            response.params?.first()?.sync?.data?.let { safeDataList ->
                handleGetPlaylistResponse(safeDataList)
            }
        }
    }

    private suspend fun handleGetPlaylistResponse(dataItemList: List<DataItem>) {
        val mustDownloadList = mutableListOf<DataItem>()
        dataItemList.forEach { dataItem ->
            var isDownloaded = false
            if (getDownloadedFiles(applicationContext).isEmpty()) mustDownloadList.add(dataItem)
            else {
                getDownloadedFiles(applicationContext).forEach { downloadedFile ->
                    if (dataItem.name == downloadedFile) isDownloaded = true
                }
                if (!isDownloaded) mustDownloadList.add(dataItem)
            }
        }
        if (mustDownloadList.isNotEmpty()) downloadMedias(mustDownloadList)
        else updateDbData(dataItemList)
    }

    private suspend fun downloadMedias(dataItemList: List<DataItem>) {
        val didNotDownloadedList = mutableListOf<DataItem>()
        var currentDownloadIndex = 0
        val dataItem = dataItemList[currentDownloadIndex]
        currentDownloadIndex++
        suspend fun goNextDownload() {
            if (currentDownloadIndex < dataItemList.size) {
                downloadMedias(
                    dataItemList
                )
            } else {
                val tempList = mutableListOf<DataItem>()
                dataItemList.forEach { dataItem ->
                    var flag = false
                    didNotDownloadedList.forEach { didNotDownloadedItem ->
                        if (didNotDownloadedItem == dataItem) {
                            flag = true
                        }
                    }
                    if (!flag) {
                        tempList.add(dataItem)
                    }
                }
                updateDbData(tempList)
            }
        }
        withContext(Dispatchers.IO) {
            try {
                val url = "https://octopus-panel-case.azurewebsites.net/uploads/${dataItem.name}"
                val downloadResult =
                    downloadManager.downloadMedia(url, dataItem.name)
                if (downloadResult == DownloadManager.DownloadResult.Exception) didNotDownloadedList.add(dataItem)
                goNextDownload()
            } catch (e: Exception) {
                didNotDownloadedList.add(dataItem)
                goNextDownload()
                Log.e("ersincoskun", "my exception: " + e.localizedMessage)
            } catch (e: IOException) {
                didNotDownloadedList.add(dataItem)
                goNextDownload()
                Log.e("ersincoskun", "my exception: " + e.localizedMessage)
            }
        }
    }

    private suspend fun updateDbData(dataListForInsert: List<DataItem>) {
        playlistDao.deletePlaylist()
        playlistDao.insertPlaylist(dataListForInsert)
    }

    private fun getDownloadedFiles(context: Context): List<String> {
        val path = context.filesDir.toString() + "/MediaFiles"
        val fileNameList = mutableListOf<String>()
        val directory = File(path)
        val files = directory.listFiles()
        files?.let {
            it.forEach { file ->
                fileNameList.add(file.name)
            }
        }
        return fileNameList
    }
}