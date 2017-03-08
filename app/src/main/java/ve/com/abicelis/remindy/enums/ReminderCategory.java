package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderCategory {
    BUSINESS("Business"),
    PERSONAL("Personal");

    private String friendlyName;
    ReminderCategory(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public ReminderCategory getByFriendlyName(String friendlyName) {
        for (ReminderCategory rc : ReminderCategory.values()) {
            if(rc.getFriendlyName().equals(friendlyName))
                return rc;
        }
        throw new IllegalArgumentException(friendlyName + " is not a valid ReminderCategory.");
    }
}
