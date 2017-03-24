package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderDateType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderTimeType;
import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 3/3/2017.
 */

public class AdvancedReminder extends Reminder implements Serializable {

    private Place place;
    private ReminderDateType dateType;
    private Calendar startDate;
    private Calendar endDate;
    private ReminderTimeType timeType;
    private Time startTime;
    private Time endTime;
    private Weekdays weekdays;

    public AdvancedReminder(@NonNull ReminderStatus status, @NonNull String title, @NonNull String description,
                            @NonNull ReminderCategory category, @Nullable Place place, @NonNull ReminderDateType dateType,
                            @Nullable Calendar startDate, @Nullable Calendar endDate, @NonNull ReminderTimeType timeType,
                            @Nullable Time startTime, @Nullable Time endTime, @NonNull Weekdays weekdays) {
        super(status, title, description, category);
        init(place, dateType, startDate, endDate, timeType, startTime, endTime, weekdays);
    }

    public AdvancedReminder(@NonNull int id, @NonNull ReminderStatus status, @NonNull String title, @NonNull String description,
                            @NonNull ReminderCategory category, @Nullable Place place, @NonNull ReminderDateType dateType,
                            @Nullable Calendar startDate, @Nullable Calendar endDate, @NonNull ReminderTimeType timeType,
                            @Nullable Time startTime, @Nullable Time endTime, @NonNull Weekdays weekdays) {
        super(id, status, title, description, category);
        init(place, dateType, startDate, endDate, timeType, startTime, endTime, weekdays);
    }

    private void init(Place place, ReminderDateType dateType, Calendar startDate, Calendar endDate,
                      ReminderTimeType timeType, Time startTime, Time endTime, Weekdays weekdays) {
        this.place = place;
        this.dateType = dateType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeType = timeType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekdays = weekdays;
    }



    @Override
    public ReminderType getType() {
        return ReminderType.ADVANCED;
    }



    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }

    public ReminderDateType getDateType() {
        return dateType;
    }
    public void setDateType(ReminderDateType dateType) {
        this.dateType = dateType;
    }

    public Calendar getStartDate() {
        return startDate;
    }
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public ReminderTimeType getTimeType() {
        return timeType;
    }
    public void setTimeType(ReminderTimeType timeType) {
        this.timeType = timeType;
    }

    public Time getStartTime() {
        return startTime;
    }
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Weekdays getWeekdays() {
        return weekdays;
    }
    public void setWeekdays(Weekdays weekdays) {
        this.weekdays = weekdays;
    }

    @Override
    public String toString() {

        String                  res = "ID=" + id + "\r\n title=" + title + "\r\n description=" + description + "\r\n";
        if(status != null)      res += " status=" + status.name() + "\r\n";
        if(category != null)    res +=  " category=" + category.name() + "\r\n";
        if(place != null)    res +=  " place=" + place.toString() + "\r\n";
        if(dateType != null)    res +=  " dateType=" + dateType.name() + "\r\n";
        if(startDate != null)    res +=  " startDate=" + startDate.toString() + "\r\n";
        if(endDate != null)    res +=  " endDate=" + endDate.toString() + "\r\n";
        if(timeType != null)    res +=  " timeType=" + timeType.name() + "\r\n";
        if(startTime != null)    res +=  " startTime=" + startTime.toString() + "\r\n";
        if(endTime != null)    res +=  " endTime=" + endTime.toString() + "\r\n";
        if(weekdays != null)    res +=  " weekDays=" + weekdays.toString() + "\r\n";
        if(extras != null)    res +=  " #extras=" + extras.size();

        return res;
    }
}
