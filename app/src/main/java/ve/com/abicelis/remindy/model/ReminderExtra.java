package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

abstract class ReminderExtra {
    private int id;
    private int reminderId;

    public ReminderExtra(int id, int reminderId) {
        this.id = id;
        this.reminderId = reminderId;
    }

    public abstract ReminderExtraType getType();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getReminderId() {
        return reminderId;
    }
    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }
}
