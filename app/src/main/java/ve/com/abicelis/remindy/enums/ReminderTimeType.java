package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderTimeType {
    ANYTIME("Anytime"),
    SINGLE_TIME("Set time"),
    INTERVAL("Set interval");

    private String description;
    ReminderTimeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public ReminderTimeType getByDescription(String description) {
        for (ReminderTimeType rc : ReminderTimeType.values()) {
            if(rc.getDescription().equals(description))
                return rc;
        }
        throw new IllegalArgumentException(description + " is not a valid ReminderTimeType.");
    }
}
