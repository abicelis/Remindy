package ve.com.abicelis.remindy.viewmodel;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.util.CalendarUtil;

/**
 * Created by abice on 26/3/2017.
 */

public class TaskTriggerViewModel implements Serializable {

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

    public Calendar getTriggerDateWithTime() {
        Calendar cal = CalendarUtil.getNewInstanceZeroedCalendar();
        cal.setTimeInMillis(getTriggerDate().getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, getTriggerTime().getHour());
        cal.set(Calendar.MINUTE, getTriggerTime().getMinute());

        return cal;
    }
}
