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

    public OneTimeReminder(int id, @NonNull Calendar date, @NonNull Time time, @NonNull ReminderRepeatType repeatType, int repeatInterval,
                           @Nullable ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        super(id);
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
        if(date != null)            res +=  " Date=" + date.toString() + "\r\n";
        if(time != null)            res +=  " Time=" + time.toString();
        return res;
    }
}
