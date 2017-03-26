package ve.com.abicelis.remindy.model.reminder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Time;

/**
 * Created by abice on 3/3/2017.
 */

public class OneTimeReminder extends Reminder implements Serializable {

    private Calendar date;
    private Time time;


    public OneTimeReminder(@NonNull Calendar date, @NonNull Time time) {
        init(date, time);
    }

    public OneTimeReminder(int id, int taskId, @NonNull Calendar date, @NonNull Time time) {
        super(id, taskId);
        init(date, time);
    }

    private void init(@NonNull Calendar date, @NonNull Time time) {
        this.date = date;
        this.time = time;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.ONE_TIME;
    }

    public Calendar getDate() {
        return date;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }
    public void setTime(Time time) {
        this.time = time;
    }


    @Override
    public String toString() {
        String                      res = "Reminder ID=" + getId() + "\r\n";
                                    res += " Type=" + getType().name() + "\r\n";
                                    res += " TaskID=" + getTaskId() + "\r\n";
                                    res +=  " Date=" + date.toString() + "\r\n";
                                    res +=  " Time=" + time.toString();
        return res;
    }
}
