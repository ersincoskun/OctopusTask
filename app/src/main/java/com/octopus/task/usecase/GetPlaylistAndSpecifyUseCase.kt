package com.octopus.task.usecase

import android.content.Context
import android.util.Log
import com.octopus.task.manager.DownloadManager
import com.octopus.task.model.*
import com.octopus.task.repo.CommonRepository
import com.octopus.task.utils.Resource
import com.octopus.task.utils.printErrorLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class GetPlaylistAndSpecifyUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val commonRepository: CommonRepository,
    private val downloadManager: DownloadManager
) {

    private var currentDownloadIndex = 0

    private suspend fun getPlayList() {
        val resource = commonRepository.getPlaylistFromServer()
        if (resource is Resource.Success<*>) {
            val response = resource.data as ResponseModel
            var sync: Sync? = null
            var report: Report? = null
            response.params?.forEach { paramsItem ->
                paramsItem.report?.let { safeReport ->
                    report = safeReport
                }

                paramsItem.sync?.let { safeSync ->
                    sync = safeSync
                }
            }
            sync?.data?.let { safeDataList ->
                handleGetPlaylistResponse(safeDataList)
                sync?.command_id?.let { safeCommandId ->
                    commonRepository.specify(SpecifyBodyModel(safeCommandId.toString()))
                }
            }

            report?.command_id?.let { safeReportCommandId ->
                commonRepository.specify(SpecifyBodyModel(safeReportCommandId.toString()))
            }
        }
    }

    private suspend fun handleGetPlaylistResponse(dataItemList: List<DataItem>) {
        val mustDownloadList = mutableListOf<DataItem>()
        dataItemList.forEach { dataItem ->
            var isDownloaded = false
            if (getDownloadedFiles(context).isEmpty()) mustDownloadList.add(dataItem)
            else {
                getDownloadedFiles(context).forEach { downloadedFile ->
                    if (dataItem.name == downloadedFile) isDownloaded = true
                }
                if (!isDownloaded) mustDownloadList.add(dataItem)
            }
        }
        if (mustDownloadList.isNotEmpty()) downloadMedias(mustDownloadList, dataItemList)
        else updateDbData(dataItemList)
    }

    private suspend fun downloadMedias(mustDownloadList: List<DataItem>, dataItemList: List<DataItem>) {
        val didNotDownloadedList = mutableListOf<DataItem>()
        val dataItem = mustDownloadList[currentDownloadIndex]
        currentDownloadIndex++
        suspend fun goNextDownload() {
            printErrorLog("currentDownloadedIndex: $currentDownloadIndex list size: ${dataItemList.size}")
            if (currentDownloadIndex < mustDownloadList.size) {
                downloadMedias(
                    mustDownloadList,
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
                if (downloadResult == DownloadManager.DownloadResult.Fail) didNotDownloadedList.add(dataItem)
                goNextDownload()
            } catch (e: Exception) {
                didNotDownloadedList.add(dataItem)
                goNextDownload()
                printErrorLog("download error: $e")
            } catch (e: IOException) {
                didNotDownloadedList.add(dataItem)
                goNextDownload()
                printErrorLog("download error: $e")
            }
        }
    }

    private suspend fun updateDbData(dataListForInsert: List<DataItem>) {
        printErrorLog("inserted to db list: $dataListForInsert")
        commonRepository.deletePlaylistFromDB()
        commonRepository.insertPlaylistToDb(dataListForInsert)
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

    suspend operator fun invoke(): UseCaseResult = withContext(Dispatchers.IO) {
        getPlayList()
        UseCaseResult.Success
    }
}