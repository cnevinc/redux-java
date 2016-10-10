package com.android

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyboard() {
    inputMethodManager(this.context).hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    inputMethodManager(this.context).showSoftInputFromInputMethod(windowToken, 0)
}

private fun inputMethodManager(application: Context): InputMethodManager {
    return application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
