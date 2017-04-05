package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderType implements Serializable {
    NONE(R.string.reminder_type_none),
    ONE_TIME(R.string.reminder_type_one_time),
    REPEATING(R.string.reminder_type_repeating),
    LOCATION_BASED(R.string.reminder_type_location_based);

    private @StringRes
    int friendlyNameRes;

    ReminderType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderType rt : values()) {
            friendlyValues.add(context.getResources().getString(rt.friendlyNameRes));
        }
        return friendlyValues;
    }
}
