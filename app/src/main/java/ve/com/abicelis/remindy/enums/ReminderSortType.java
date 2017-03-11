package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderSortType {
    DATE("Date"),
    PLACE("Place"),
    CATEGORY("Category");

    private String friendlyName;
    ReminderSortType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public ReminderSortType getByFriendlyName(String friendlyName) {
        for (ReminderSortType rc : ReminderSortType.values()) {
            if(rc.getFriendlyName().equals(friendlyName))
                return rc;
        }
        throw new IllegalArgumentException(friendlyName + " is not a valid ReminderSortType.");
    }
}
