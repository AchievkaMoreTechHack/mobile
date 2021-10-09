package com.achievka.moretech3.extentions

import android.content.Context

fun Context.dpToPx(dp: Int) : Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}