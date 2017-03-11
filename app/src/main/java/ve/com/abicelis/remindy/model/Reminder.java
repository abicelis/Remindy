package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderDateType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderTimeType;

/**
 * Created by abice on 3/3/2017.
 */

public class Reminder {

    private int id;
    private ReminderStatus status;
    private String title;
    private String description;
    private ReminderCategory category;
    private Place place;
    private ReminderDateType dateType;
    private Calendar startDate;
    private Calendar endDate;
    private ReminderTimeType timeType;
    private Time startTime;
    private Time endTime;
    private List<ReminderExtra> extras;


    public Reminder(@NonNull int id, @NonNull ReminderStatus status, @NonNull String title,
                    @Nullable String description, @NonNull ReminderCategory category, @Nullable Place place,
                    @NonNull ReminderDateType dateType, @Nullable Calendar startDate, @Nullable Calendar endDate,
                    @NonNull ReminderTimeType timeType, @Nullable Time startTime, @Nullable Time endTime) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.category = category;
        this.place = place;
        this.dateType = dateType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeType = timeType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ReminderStatus getStatus() {
        return status;
    }
    public void setStatus(boolean active) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public ReminderCategory getCategory() {
        return category;
    }
    public void setCategory(ReminderCategory category) {
        this.category = category;
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

    public List<ReminderExtra> getExtras() {
        return extras;
    }
    public void setExtras(List<ReminderExtra> extras) {
        this.extras = extras;
    }


    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " status=" + status.getFriendlyName() + "\r\n" +
                " title=" + title + "\r\n" +
                " description=" + description + "\r\n" +
                " category=" + category.getFriendlyName() + "\r\n" +
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
