package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderCategory {
    BUSINESS(R.string.reminder_category_business),
    PERSONAL(R.string.reminder_category_personal);

    private @StringRes int friendlyNameRes;

    ReminderCategory(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderCategory rc : values()) {
            friendlyValues.add(context.getResources().getString(rc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
