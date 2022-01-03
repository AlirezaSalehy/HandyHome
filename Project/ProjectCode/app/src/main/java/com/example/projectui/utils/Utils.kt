package com.example.projectui.utils

import android.content.res.Resources

fun pxToDp(num: Int): Int {
    return (num / Resources.getSystem().displayMetrics.density).toInt()
}

fun dpTpPx(num: Int): Int {
    return (num * Resources.getSystem().displayMetrics.density).toInt()

}

fun spToPx(num: Int): Int {
    return (num / Resources.getSystem().displayMetrics.scaledDensity).toInt()
}
