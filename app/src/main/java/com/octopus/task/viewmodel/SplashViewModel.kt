package com.octopus.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.repo.CommonRepository
import com.octopus.task.usecase.GetPlaylistAndSpecifyUseCase
import com.octopus.task.usecase.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SplashViewModel @Inject constructor(
    val preferencesHelper: PreferencesHelper,
    private val commonRepository: CommonRepository,
    private val getPlaylistAndSpecifyUseCase: GetPlaylistAndSpecifyUseCase
) : ViewModel() {

    private lateinit var mJob: Job

    private val _isReadyToStart = MutableLiveData<Boolean>()
    val isReadyToStart: LiveData<Boolean>
        get() = _isReadyToStart

    private fun checkIsReadyToPlay() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getPlaylistAndSpecifyUseCase()
            if (result == UseCaseResult.Success) {
                val playlist = commonRepository.getPlaylistFromDb()
                _isReadyToStart.value = playlist.isNotEmpty()
            }
        }
    }

    fun startRequestLoop() {
        mJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(5 * 1000)
                checkIsReadyToPlay()
            }
        }
    }

    fun stopRequestLoop(){
        mJob.cancel()
    }

    fun generateAlphaNumericId(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..7)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }
}