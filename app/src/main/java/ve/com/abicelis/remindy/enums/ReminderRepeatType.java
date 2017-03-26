package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 16/3/2017.
 */

public enum ReminderRepeatType {
    DAILY(R.string.reminder_repeat_type_daily),
    WEEKLY(R.string.reminder_repeat_type_weekly),
    MONTHLY(R.string.reminder_repeat_type_monthly),
    YEARLY(R.string.reminder_repeat_type_yearly);

    private @StringRes
    int friendlyNameRes;

    ReminderRepeatType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderRepeatType rc : values()) {
            friendlyValues.add(context.getResources().getString(rc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
