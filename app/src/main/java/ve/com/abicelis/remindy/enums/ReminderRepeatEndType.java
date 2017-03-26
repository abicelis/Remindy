package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 16/3/2017.
 */

public enum ReminderRepeatEndType {
    FOREVER(R.string.reminder_repeat_end_type_forever),
    UNTIL_DATE(R.string.reminder_repeat_end_type_until_date),
    FOR_X_EVENTS(R.string.reminder_repeat_end_type_for_x_events);

    private @StringRes
    int friendlyNameRes;

    ReminderRepeatEndType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderRepeatEndType rc : values()) {
            friendlyValues.add(context.getResources().getString(rc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
