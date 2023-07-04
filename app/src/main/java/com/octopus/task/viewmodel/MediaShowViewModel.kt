package com.octopus.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.repo.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MediaShowViewModel
@Inject constructor(
    val preferencesHelper: PreferencesHelper,
    private val splashRepository: SplashRepository
) : ViewModel() {

    private val _playlist = MutableLiveData<List<PlaylistModel>>()
    val playlist: LiveData<List<PlaylistModel>>
        get() = _playlist

    fun getPlaylistFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = splashRepository.getPlaylistFromDb()
            withContext(Dispatchers.Main) {
                _playlist.value = playlist
            }
        }
    }

    private fun isPlaylistItemInInterval(startDateString: String, endDateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = dateFormat.parse(startDateString)
        val endDate = dateFormat.parse(endDateString)
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        return if (startDate != null && endDate != null) {
            startCalendar.time = startDate
            endCalendar.time = endDate
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time
            currentTime.after(startCalendar.time) && currentTime.before(endCalendar.time)
        } else {
            false
        }
    }
}