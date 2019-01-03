package com.igorronner.irinterstitial.utils

import android.content.Context
import android.content.pm.PackageManager



fun Context.appIsInstalled(packageName:String):Boolean{
    return try {
        this.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}