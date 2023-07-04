package com.octopus.task.helpers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.octopus.task.utils.Constants.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PreferencesHelper @Inject constructor(@ApplicationContext var context: Context) {

    companion object {
        const val PLAYLIST_ORDER = "PLAYLIST_ORDER"
        const val VIDEO_PLAYLIST_ORDER = "VIDEO_PLAYLIST_ORDER"
        const val DEVICE_ID = "DEVICE_ID"
    }

    private var sharedPreferences = createSharedPreferences(context)

    private fun createSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun getSharedPrefsValue(
        key: String,
        type: Class<*>,
        defaultLong: Long = 0,
        defaultString: String = "",
        defaultInt: Int = 0,
        defaultFloat: Float = 0f,
        defaultBoolean: Boolean = false
    ): Any? {
        return when (type) {
            String::class.java -> sharedPreferences.getString(key, defaultString)
            Long::class.java -> sharedPreferences.getLong(key, defaultLong)
            Int::class.java -> sharedPreferences.getInt(key, defaultInt)
            Float::class.java -> sharedPreferences.getFloat(key, defaultFloat)
            Boolean::class.java -> sharedPreferences.getBoolean(key, defaultBoolean)
            else -> {
                println("out of type")
                Log.e("TAG", "shared preferences out of type")
                null
            }
        }
    }

    private fun savePrefValue(key: String, value: Any?) {
        val editor = sharedPreferences.edit()
        editor.apply {
            if (value == null) {
                remove(key)
                apply()
                return
            }
            when (value) {
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }

    }

    fun removeValue(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key).apply()
    }

    var playlistOrder: Int
        get() = getSharedPrefsValue(
            PLAYLIST_ORDER,
            Int::class.java
        ) as Int
        set(value) = savePrefValue(PLAYLIST_ORDER, value)

    var videoPlaylistOrder: Int
        get() = getSharedPrefsValue(
            VIDEO_PLAYLIST_ORDER,
            Int::class.java
        ) as Int
        set(value) = savePrefValue(VIDEO_PLAYLIST_ORDER, value)

    var deviceId: String
        get() = getSharedPrefsValue(
            DEVICE_ID,
            String()::class.java,
            defaultInt = 0
        ) as String
        set(value) = savePrefValue(DEVICE_ID, value)

}