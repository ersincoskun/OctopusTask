package com.octopus.task.utils

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.service.CheckPlaylistUpdateWorker
import com.octopus.task.utils.Constants.QR_SCANNER_LOG_TAG
import com.octopus.task.utils.Constants.UNIQUE_WORK_ID
import com.octopus.task.utils.ProjectSettings.IS_GENERAL_LOG_ENABLE
import java.util.concurrent.TimeUnit

fun printLog(logText: String) {
    if (IS_GENERAL_LOG_ENABLE) Log.d(QR_SCANNER_LOG_TAG, logText)
}

fun printErrorLog(logText: String) {
    if (IS_GENERAL_LOG_ENABLE) Log.e(QR_SCANNER_LOG_TAG, logText)
}

fun setWorkManager(pref: PreferencesHelper, context: Context) {
    val uploadWorkRequest: OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<CheckPlaylistUpdateWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag("check_update_worker")
            .build()
    //pref.setCurrentWorkId(uploadWorkRequest.id.toString())
    WorkManager
        .getInstance(context)
        .enqueueUniqueWork(UNIQUE_WORK_ID, ExistingWorkPolicy.REPLACE, uploadWorkRequest)
    //pref.setIsStartedWorkManager(true)
}

