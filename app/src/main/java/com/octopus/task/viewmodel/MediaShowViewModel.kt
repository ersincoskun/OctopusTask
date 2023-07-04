package com.octopus.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.repo.CommonRepository
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
    private val preferencesHelper: PreferencesHelper,
    private val commonRepository: CommonRepository
) : ViewModel() {

    private val _playlist = MutableLiveData<List<DataItem>>()
    val playlist: LiveData<List<DataItem>>
        get() = _playlist

    fun getPlaylistFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = commonRepository.getPlaylistFromDb()
            withContext(Dispatchers.Main) {
                _playlist.value = playlist
            }
        }
    }

    fun isItemInInterval(startDateString: String, endDateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone =  TimeZone.getDefault()
        val startDate = dateFormat.parse(startDateString)
        val endDate = dateFormat.parse(endDateString)
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        startCalendar.time = startDate
        endCalendar.time = endDate
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        return currentTime.after(startCalendar.time) && currentTime.before(endCalendar.time)
    }
}