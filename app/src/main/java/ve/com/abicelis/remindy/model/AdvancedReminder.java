package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.BitSet;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderDateType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderTimeType;
import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 3/3/2017.
 */

public class AdvancedReminder extends Reminder {

    private Place place;
    private ReminderDateType dateType;
    private Calendar startDate;
    private Calendar endDate;
    private ReminderTimeType timeType;
    private Time startTime;
    private Time endTime;
    //private BitSet weekday;
    //TODO: private Weekday weekday!!!!!!!!!!!!!!


    public AdvancedReminder(@NonNull ReminderStatus status, @NonNull String title, @NonNull String description,
                            @NonNull ReminderCategory category, @Nullable Place place, @NonNull ReminderDateType dateType,
                            @Nullable Calendar startDate, @Nullable Calendar endDate, @NonNull ReminderTimeType timeType,
                            @Nullable Time startTime, @Nullable Time endTime) {
        super(status, title, description, category);
        init(place, dateType, startDate, endDate, timeType, startTime, endTime);
    }

    public AdvancedReminder(@NonNull int id, @NonNull ReminderStatus status, @NonNull String title, @NonNull String description,
                            @NonNull ReminderCategory category, @Nullable Place place, @NonNull ReminderDateType dateType,
                            @Nullable Calendar startDate, @Nullable Calendar endDate, @NonNull ReminderTimeType timeType,
                            @Nullable Time startTime, @Nullable Time endTime) {
        super(id, status, title, description, category);
        init(place, dateType, startDate, endDate, timeType, startTime, endTime);
    }

    private void init(Place place, ReminderDateType dateType, Calendar startDate, Calendar endDate,
                      ReminderTimeType timeType, Time startTime, Time endTime) {
        this.place = place;
        this.dateType = dateType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeType = timeType;
        this.startTime = startTime;
        this.endTime = endTime;


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


    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " status=" + status.name() + "\r\n" +
                " title=" + title + "\r\n" +
                " description=" + description + "\r\n" +
                " category=" + category.name() + "\r\n" +
                " place=" + place + "\r\n" +
                " dateType=" + dateType + "\r\n" +
                " startDate=" + startDate + "\r\n" +
                " endDate=" + endDate + "\r\n" +
                " timeType=" + timeType + "\r\n" +
                " startTime=" + startTime + "\r\n" +
                " endTime=" + endTime + "\r\n" +
                " extras=" + extras;
    }
}
