package com.igorronner.irinterstitial.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.DisplayMetrics





fun Context.appIsInstalled(packageName:String):Boolean{
    return try {
        this.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}