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

    private val _isReadyToStart = MutableLiveData<Boolean>()
    val isReadyToStart: LiveData<Boolean>
        get() = _isReadyToStart

    private val _isTherePlaylist = MutableLiveData<Boolean>()
    val isTherePlaylist: LiveData<Boolean>
        get() = _isTherePlaylist

    fun checkIsTherePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = commonRepository.getPlaylistFromDb()
            withContext(Dispatchers.Main) {
                _isTherePlaylist.value = playlist.isNotEmpty()
            }
        }
    }

     fun sendRequest(isMustDeletePlaylist: Boolean = false) {
         viewModelScope.launch(Dispatchers.IO) {
             if (isMustDeletePlaylist) commonRepository.deletePlaylistFromDB()
         }
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            val result = getPlaylistAndSpecifyUseCase()
            if (result == UseCaseResult.Success) {
                val playlist = commonRepository.getPlaylistFromDb()
                withContext(Dispatchers.Main) {
                    _isReadyToStart.value = playlist.isNotEmpty()
                }
            }
        }
    }

    fun generateAlphaNumericId(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..7)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }
}