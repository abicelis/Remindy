package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.DistanceFormat;

/**
 * Created by abice on 1/4/2017.
 */

public class SharedPreferenceUtil {

    public static DistanceFormat getDistanceFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return DistanceFormat.valueOf(preferences.getString(context.getResources().getString(R.string.settings_distance_format_key), null));
    }
    public static void setDistanceFormat(DistanceFormat distanceFormat, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_distance_format_key), distanceFormat.name());
        editor.apply();
    }

}
