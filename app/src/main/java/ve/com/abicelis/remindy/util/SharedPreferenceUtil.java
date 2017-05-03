package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.enums.TimeFormat;
import ve.com.abicelis.remindy.enums.TriggerMinutesBeforeNotificationType;
import ve.com.abicelis.remindy.model.Time;

/**
 * Created by abice on 1/4/2017.
 */

public class SharedPreferenceUtil {

    public static DateFormat getDateFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dfPref = preferences.getString(context.getResources().getString(R.string.settings_date_format_key), null);
        if(dfPref == null) {
            Log.d("SharedPreferenceUtil", "getDateFormat() found null, setting PRETTY_DATE");
            DateFormat df = DateFormat.PRETTY_DATE;
            setDateFormat(df, context);
            return df;
        }
        else return DateFormat.valueOf(dfPref);
    }

    public static void setDateFormat(DateFormat df, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_date_format_key), df.name());
        editor.apply();
    }

    public static TimeFormat getTimeFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tfPref = preferences.getString(context.getResources().getString(R.string.settings_time_format_key), null);
        if(tfPref == null) {
            Log.d("SharedPreferenceUtil", "getTimeFormat() found null, setting FORMAT_24H");
            TimeFormat tf = TimeFormat.FORMAT_24H;
            setTimeFormat(tf, context);
            return tf;
        }
        else return TimeFormat.valueOf(tfPref);
    }

    public static void setTimeFormat(TimeFormat tf, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_time_format_key), tf.name());
        editor.apply();
    }



    public static TriggerMinutesBeforeNotificationType getTriggerMinutesBeforeNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tmPref = preferences.getString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), null);
        if(tmPref == null) {
            Log.d("SharedPreferenceUtil", "getTriggerMinutesBeforeNotification() found null, setting 5 minutes");
            TriggerMinutesBeforeNotificationType tm = TriggerMinutesBeforeNotificationType.MINUTES_5;
            setTriggerMinutesBeforeNotification(tm, context);
            return tm;
        }
        else return TriggerMinutesBeforeNotificationType.valueOf(tmPref);
    }

    public static void setTriggerMinutesBeforeNotification(TriggerMinutesBeforeNotificationType triggerMinutesBeforeNotification, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), triggerMinutesBeforeNotification.name());
        editor.apply();
    }

}
