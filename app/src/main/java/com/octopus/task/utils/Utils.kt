package com.octopus.task.utils

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.octopus.task.R
import com.octopus.task.utils.Constants.QR_SCANNER_LOG_TAG
import java.text.NumberFormat
import java.util.*

fun printLog(logText: String) {
    Log.d(QR_SCANNER_LOG_TAG, logText)
}

fun printErrorLog(logText: String) {
    Log.e(QR_SCANNER_LOG_TAG, logText)
}

