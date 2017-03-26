package ve.com.abicelis.remindy.model.reminder;

import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 24/3/2017.
 */

public abstract class Reminder {

    private int id;
    private int taskId;

    public Reminder(){
        this.id = -1;
        this.taskId = -1;
    }

    public Reminder(int id, int taskId) {
        this.id = id;
        this.taskId = taskId;
    }

    public abstract ReminderType getType();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
