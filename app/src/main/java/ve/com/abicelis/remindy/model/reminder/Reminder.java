package ve.com.abicelis.remindy.model.reminder;

import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 24/3/2017.
 */

public abstract class Reminder {

    private int id;

    public Reminder(){
        this.id = -1;
    }

    public Reminder(int id) {
        this.id = id;
    }

    public abstract ReminderType getType();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
