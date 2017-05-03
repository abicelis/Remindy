package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/5/2017.
 */

public enum TriggerMinutesBeforeNotificationType {
    MINUTES_1(1),
    MINUTES_5(5),
    MINUTES_10(10),
    MINUTES_20(20);

    private int mMinutes;

    TriggerMinutesBeforeNotificationType(int minutes) {
        mMinutes = minutes;
    }

    public int getMinutes() {
        return mMinutes;
    }
}
