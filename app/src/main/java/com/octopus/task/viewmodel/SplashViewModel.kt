package com.octopus.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.repo.SplashRepository
import com.octopus.task.usecase.GetPlaylistAndSpecifyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SplashViewModel @Inject constructor(
    val preferencesHelper: PreferencesHelper,
    private val splashRepository: SplashRepository,
    private val getPlaylistAndSpecifyUseCase: GetPlaylistAndSpecifyUseCase
) : ViewModel() {

    private lateinit var job: Job

    fun checkIsReadyToPlay() {
        viewModelScope.launch(Dispatchers.IO) {
           getPlaylistAndSpecifyUseCase()

        }
    }

    fun startRequestLoop() {
        job = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(1000)
                // Burada tekrarlanması gereken işleminizi yapabilirsiniz.
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