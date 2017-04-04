package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 3/3/2017.
 */

public enum TaskCategory {
    BUSINESS(R.string.task_category_business, R.drawable.icon_category_business),
    PERSONAL(R.string.task_category_personal, R.drawable.icon_category_personal),
    HEALTH(R.string.task_category_health, R.drawable.icon_category_health),
    REPAIRS(R.string.task_category_repairs, R.drawable.icon_category_repairs),
    SHOPPING(R.string.task_category_shopping, R.drawable.icon_category_shopping);

    private @StringRes int friendlyNameRes;
    private @DrawableRes int iconRes;

    TaskCategory(@StringRes int friendlyNameRes, @DrawableRes int iconRes) {
        this.friendlyNameRes = friendlyNameRes;
        this.iconRes = iconRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }
    public int getIconRes() {
        return iconRes;
    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (TaskCategory tc : values()) {
            friendlyValues.add(context.getResources().getString(tc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
