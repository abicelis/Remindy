package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 3/3/2017.
 */

public class SimpleReminder extends Reminder {

    private Calendar date;
    private Time time;
    private ReminderRepeatType repeatType;
    private int repeatInterval;
    private ReminderRepeatEndType repeatEndType;
    private int repeatEndNumberOfEvents;
    private Calendar repeatEndDate;


    public SimpleReminder(@NonNull ReminderStatus status, @NonNull String title, @NonNull String description, @NonNull ReminderCategory category,
                          @NonNull Calendar date, @NonNull Time time, @Nullable ReminderRepeatType repeatType, int repeatInterval,
                          @Nullable ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        super(status, title, description, category);
        init(date, time, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, repeatEndDate);
    }

    public SimpleReminder(@NonNull int id, @NonNull ReminderStatus status, @NonNull String title, @NonNull String description, @NonNull ReminderCategory category,
                          @NonNull Calendar date, @NonNull Time time, @Nullable ReminderRepeatType repeatType, int repeatInterval,
                          @Nullable ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        super(id, status, title, description, category);
        init(date, time, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, repeatEndDate);
    }

    private void init(Calendar date, Time time, ReminderRepeatType repeatType, int repeatInterval,
                      ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, Calendar repeatEndDate) {
        this.date = date;
        this.time = time;
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
        this.repeatEndType = repeatEndType;
        this.repeatEndNumberOfEvents = repeatEndNumberOfEvents;
        this.repeatEndDate = repeatEndDate;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.ADVANCED;
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

    public ReminderRepeatType getRepeatType() {
        return repeatType;
    }
    public void setRepeatType(ReminderRepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }
    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public ReminderRepeatEndType getRepeatEndType() {
        return repeatEndType;
    }
    public void setRepeatEndType(ReminderRepeatEndType repeatEndType) {
        this.repeatEndType = repeatEndType;
    }

    public int getRepeatEndNumberOfEvents() {
        return repeatEndNumberOfEvents;
    }
    public void setRepeatEndNumberOfEvents(int repeatEndNumberOfEvents) {
        this.repeatEndNumberOfEvents = repeatEndNumberOfEvents;
    }

    public Calendar getRepeatEndDate() {
        return repeatEndDate;
    }
    public void setRepeatEndDate(Calendar repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }



    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " status=" + status.name() + "\r\n" +
                " title=" + title + "\r\n" +
                " description=" + description + "\r\n" +
                " category=" + category.name() + "\r\n" +
                " date=" + date + "\r\n" +
                " time=" + time + "\r\n" +
                " repeatType=" + repeatType.toString() + "\r\n" +
                " repeatInterval=" + repeatInterval + "\r\n" +
                " repeatEndType=" + repeatEndType.toString() + "\r\n" +
                " repeatEndNumberOfEvents=" + repeatEndNumberOfEvents + "\r\n" +
                " repeatEndDate=" + repeatEndDate + "\r\n" +
                " extras=" + extras;
    }
}
