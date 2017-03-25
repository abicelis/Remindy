package ve.com.abicelis.remindy.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 3/3/2017.
 */

public enum TaskSortType {
    DATE(R.string.task_sort_type_date),
    PLACE(R.string.task_sort_type_place);

    private @StringRes
    int friendlyNameRes;

    TaskSortType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (TaskSortType tst : values()) {
            friendlyValues.add(context.getResources().getString(tst.friendlyNameRes));
        }
        return friendlyValues;
    }
}
