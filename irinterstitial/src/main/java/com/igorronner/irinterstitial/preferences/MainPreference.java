package com.igorronner.irinterstitial.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.igorronner.irinterstitial.init.ConfigUtil;
import com.igorronner.irinterstitial.utils.ContextKt;

import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public class MainPreference {

    private static final String FIRST_LAUNCH_DATE = "first_launch_date";
    private static final String PREFS_PREMIUM = "prefs_premium";


    private static final String PREFS_WAS_REWARDED = "was_rewarded";

    private static final String PREFS_PREMIUM_DAYS = "PREFS_PREMIUM_DAYS";
    private static final String PREFS_PREMIUM_DAYS_START = "PREFS_PREMIUM_DAYS_START";
    private static final String PREFS_PREMIUM_DAYS_END = "PREFS_PREMIUM_DAYS_END";

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void setFirstLaunchDate(Context context, long firstLaunchDate) {
        getPreferencesEditor(context)
                .putLong(FIRST_LAUNCH_DATE, firstLaunchDate)
                .apply();
    }

    public static void setWasRewarded(Context context, boolean wasRewarded) {
        getPreferencesEditor(context)
                .putBoolean(PREFS_WAS_REWARDED, wasRewarded)
                .apply();
    }


    public static void setPremium(Context context) {
        getPreferencesEditor(context)
                .putBoolean(PREFS_PREMIUM, true)
                .apply();
    }

    public static void setDaysPremium(Context context, int days) {
        final Calendar starDaysPremium = Calendar.getInstance();

        final Calendar endDaysPremium = Calendar.getInstance();
        endDaysPremium.add(Calendar.DAY_OF_MONTH, days);

        getPreferencesEditor(context)
                .putInt(PREFS_PREMIUM_DAYS, days)
                .putLong(PREFS_PREMIUM_DAYS_START, starDaysPremium.getTimeInMillis())
                .putLong(PREFS_PREMIUM_DAYS_END, endDaysPremium.getTimeInMillis())
                .apply();
    }

    public static long getFirstLaunchDate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(FIRST_LAUNCH_DATE, 0);
    }

    public static boolean isPremium(Context context) {

        if (ConfigUtil.TESTER) {
            return false;
        }

        if (getPreferences(context).getBoolean(PREFS_PREMIUM, false)) {
            return true;
        }

        if (hasPremiumDays(context)) {
            return true;
        }

        if (isPremiumMobills(context)){
            return true;
        }

        return !ConfigUtil.AD_ENABLED;
    }

    public static boolean isPremiumMobills(Context context) {
        final boolean hasMobills = ContextKt.appIsInstalled(
                context, "br.com.gerenciadorfinanceiro.controller");
        return ConfigUtil.ENABLE_CHECK_MOBILLS && hasMobills;
    }

    public static boolean wasRewarded(Context context) {
        return  getPreferences(context).getBoolean(PREFS_WAS_REWARDED, false);
    }
    public static boolean hasPremiumDays(Context context) {
        final SharedPreferences preferences = getPreferences(context);
        final int premiumDays = preferences.getInt(PREFS_PREMIUM_DAYS, 0);
        final long premiumStartDay = preferences.getLong(PREFS_PREMIUM_DAYS_START, 0);
        final long premiumEndDay = preferences.getLong(PREFS_PREMIUM_DAYS_END, 0);

        if (premiumDays <= 0 || premiumStartDay <= 0 || premiumEndDay <= 0) {
            return false;
        }

        final Calendar today = Calendar.getInstance();
        final long todayInMillis = today.getTimeInMillis();

        final boolean hasPremiumDays = (premiumStartDay <= todayInMillis) && (todayInMillis <= premiumEndDay);

        if (!hasPremiumDays) {
            setDaysPremium(context, 0);
        }

        return hasPremiumDays;
    }

}