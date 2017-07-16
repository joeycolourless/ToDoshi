package com.android.joeycolourless.todoshi;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by admin on 05.07.2017.
 */

public class QueryPreferences {
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }
}
