package com.octopus.task.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.usecase.GetPlaylistAndSpecifyUseCase
import com.octopus.task.usecase.UseCaseResult
import com.octopus.task.utils.printErrorLog
import com.octopus.task.utils.setWorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
CheckPlaylistUpdateWorker checks is there any new playlist and if there is it download new playlist
and insert to sqlite
*/
@HiltWorker
class CheckPlaylistUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPlaylistAndSpecifyUseCase: GetPlaylistAndSpecifyUseCase,
    private val preferencesHelper: PreferencesHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        printErrorLog("worker ran")
        withContext(Dispatchers.IO) {
            //this result for wait to end of getPlaylistAndSpecifyUseCase processes.
            val useCaseResult = getPlaylistAndSpecifyUseCase()
            if (useCaseResult == UseCaseResult.Success) setWorkManager(preferencesHelper, applicationContext)
        }

        return Result.success()
    }
}