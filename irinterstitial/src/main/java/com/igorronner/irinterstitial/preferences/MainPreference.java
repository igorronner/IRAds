package com.igorronner.irinterstitial.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainPreference {

    private static final String FIRST_LAUNCH_DATE = "first_launch_date";


    public static void setFirstLaunchDate(Context context, long firstLaunchDate){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(FIRST_LAUNCH_DATE, firstLaunchDate);
        editor.commit();
    }




    public static long getFirstLaunchDate(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(FIRST_LAUNCH_DATE, 0);
    }

}