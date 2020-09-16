package com.mypet.www.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.hideKeyBoard(view: View){
    val inputMethodManager = getSystemService(
        Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager
        .hideSoftInputFromWindow(view.windowToken, 0)
}