package ve.com.abicelis.remindy.viewmodel;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.Time;

/**
 * Created by abice on 26/3/2017.
 */

public class TaskTriggerViewModel {

    //DATA
    private Task task = null;
    private Calendar triggerDate = null;
    private Time triggerTime = null;

    public TaskTriggerViewModel(@NonNull Task task, @NonNull Calendar triggerDate, @NonNull Time triggerTime) {
        this.task = task;
        this.triggerDate = triggerDate;
        this.triggerTime = triggerTime;
    }

    public Task getTask() { return task; }
    public Calendar getTriggerDate() { return triggerDate; }
    public Time getTriggerTime() { return triggerTime; }
}
