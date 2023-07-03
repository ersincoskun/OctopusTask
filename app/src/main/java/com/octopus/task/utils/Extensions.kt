package com.octopus.task.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.octopus.task.R

fun View.onSingleClickListener(listener: View.OnClickListener) {
    this.setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(view: View?) {
            listener.onClick(view)
        }
    })
}

fun Context.showSoftKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
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