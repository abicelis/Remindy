package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.enums.TimeFormat;
import ve.com.abicelis.remindy.enums.TriggerMinutesBeforeNotificationType;
import ve.com.abicelis.remindy.enums.TapTargetSequenceType;

/**
 * Created by abice on 1/4/2017.
 */

public class SharedPreferenceUtil {

    public static boolean doShowTapTargetSequenceFor(Context context, TapTargetSequenceType tapTargetSequenceType) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //boolean flag = preferences.getBoolean(tapTargetSequenceType.name(), true);
        boolean flag = true;

        //if(flag)
        //    preferences.edit().putBoolean(tapTargetSequenceType.name(), false).apply();

        return flag;
    }

    public static DateFormat getDateFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dfPref = preferences.getString(context.getResources().getString(R.string.settings_date_format_key), null);
        DateFormat pref;
        try {
            pref = DateFormat.valueOf(dfPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getDateFormat() found null, setting PRETTY_DATE");
            DateFormat df = DateFormat.PRETTY_DATE;
            setDateFormat(df, context);
            return df;
        }
        else return pref;
    }

    public static void setDateFormat(DateFormat df, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_date_format_key), df.name());
        editor.apply();
    }





    public static TimeFormat getTimeFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tfPref = preferences.getString(context.getResources().getString(R.string.settings_time_format_key), null);
        TimeFormat pref;
        try {
            pref = TimeFormat.valueOf(tfPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getTimeFormat() found null, setting FORMAT_24H");
            TimeFormat tf = TimeFormat.FORMAT_24H;
            setTimeFormat(tf, context);
            return tf;
        }
        else return pref;
    }

    public static void setTimeFormat(TimeFormat tf, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_time_format_key), tf.name());
        editor.apply();
    }




    public static TriggerMinutesBeforeNotificationType getTriggerMinutesBeforeNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tmPref = preferences.getString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), null);
        TriggerMinutesBeforeNotificationType pref;
        try {
            pref = TriggerMinutesBeforeNotificationType.valueOf(tmPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getTriggerMinutesBeforeNotification() found null, setting 5 minutes");
            TriggerMinutesBeforeNotificationType tm = TriggerMinutesBeforeNotificationType.MINUTES_5;
            setTriggerMinutesBeforeNotification(tm, context);
            return tm;
        }
        else return pref;
    }

    public static void setTriggerMinutesBeforeNotification(TriggerMinutesBeforeNotificationType triggerMinutesBeforeNotification, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), triggerMinutesBeforeNotification.name());
        editor.apply();
    }

}
