package com.android

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyboard(application : Context) {
    inputMethodManager(application).hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(application : Context) {
    inputMethodManager(application).showSoftInputFromInputMethod(windowToken, 0)
}

private fun inputMethodManager(application: Context): InputMethodManager {
    return application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
