package com.igorronner.irinterstitial.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.igorronner.irinterstitial.init.ConfigUtil;

public class MainPreference {

    private static final String FIRST_LAUNCH_DATE = "first_launch_date";
    private static final String PREFS_PREMIUM  = "prefs_premium";


    public static void setFirstLaunchDate(Context context, long firstLaunchDate){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(FIRST_LAUNCH_DATE, firstLaunchDate);
        editor.apply();
    }


    public static void setPremium(Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PREFS_PREMIUM, true);
        editor.apply();
    }


    public static long getFirstLaunchDate(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(FIRST_LAUNCH_DATE, 0);
    }

    public static boolean isPremium(Context context){
        if (ConfigUtil.TESTER)
            return false;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(PREFS_PREMIUM, false);
    }
}