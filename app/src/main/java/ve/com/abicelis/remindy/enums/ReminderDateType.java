package ve.com.abicelis.remindy.enums;

/**
 * Created by abice on 3/3/2017.
 */

public enum ReminderDateType {
    ANYDAY("Anyday"),
    SINGLE_DAY("Set date"),
    INTERVAL("Set interval");

    private String description;
    ReminderDateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public ReminderDateType getByDescription(String description) {
        for (ReminderDateType rc : ReminderDateType.values()) {
            if(rc.getDescription().equals(description))
                return rc;
        }
        throw new IllegalArgumentException(description + " is not a valid ReminderDateType.");
    }
}
