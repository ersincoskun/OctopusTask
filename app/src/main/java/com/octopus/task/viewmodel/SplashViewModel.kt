package com.octopus.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.repo.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SplashViewModel @Inject constructor(
    val preferencesHelper: PreferencesHelper,
    private val splashRepository: SplashRepository
) : ViewModel() {

    private lateinit var job: Job
    private val _playlist = MutableLiveData<List<DataItem>>()
    val playlist: LiveData<List<DataItem>>
        get() = _playlist

    fun getPlaylistFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist =
                withContext(Dispatchers.Main) {

                }
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