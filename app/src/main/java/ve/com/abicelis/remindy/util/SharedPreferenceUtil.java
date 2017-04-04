package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.enums.DistanceFormat;

/**
 * Created by abice on 1/4/2017.
 */

public class SharedPreferenceUtil {

//    public static DistanceFormat getDistanceFormat(Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String dfPref = preferences.getString(context.getResources().getString(R.string.settings_distance_format_key), null);
//        if(dfPref == null) {
//            Log.d("SharedPreferenceUtil", "getDistanceFormat() found null, setting KILOMETERS");
//            DistanceFormat df = DistanceFormat.KILOMETERS;
//            setDistanceFormat(df, context);
//            return df;
//        }
//        else return DistanceFormat.valueOf(dfPref);
//    }
//    public static void setDistanceFormat(DateFormat distanceFormat, Context context) {
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putString(context.getResources().getString(R.string.settings_distance_format_key), distanceFormat.name());
//        editor.apply();
//    }


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

}
