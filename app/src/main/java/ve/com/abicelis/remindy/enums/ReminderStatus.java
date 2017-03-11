package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderStatus {
    ARCHIVED("Archived"),
    ACTIVE("Active"),
    DONE("Done"),
    OVERDUE("Overdue");

    private String friendlyName;
    ReminderStatus(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public ReminderStatus getByFriendlyName(String friendlyName) {
        for (ReminderStatus rc : ReminderStatus.values()) {
            if(rc.getFriendlyName().equals(friendlyName))
                return rc;
        }
        throw new IllegalArgumentException(friendlyName + " is not a valid ReminderStatus.");
    }
}
