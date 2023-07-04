package com.octopus.task.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.octopus.task.R

fun Activity.setFullScreen() {
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    if (Build.VERSION.SDK_INT < 19) { // lower api
        val v = this.window.decorView
        v.systemUiVisibility = View.GONE
    } else {
        val decorView = this.window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }
}

fun View.showErrorSnackBar(context: Context?) {
    context?.let { safeContext ->
        Snackbar.make(this, safeContext.getString(R.string.default_error_text), 2500)
            .show()
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.remove() {
    this.visibility = View.GONE
}