package ve.com.abicelis.remindy.model;

import java.security.InvalidParameterException;
import java.util.Locale;

import ve.com.abicelis.remindy.enums.TimeFormat;

/**
 * Created by abice on 8/3/2017.
 */


/**
 * Note: Internally handles time always as 24H time.
 */
public class Time implements Comparable<Time> {
    private int hour;
    private int minute;
    private TimeFormat displayTimeFormat = TimeFormat.FORMAT_24H;

    public Time() {
        this.hour = 0;
        this.minute = 0;
    }
    public Time(int timeInMinutes) {
        setTimeInMinutes(timeInMinutes);
    }

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(int hour, int minute, TimeFormat displayTimeFormat) {
        this(hour, minute);
        this.displayTimeFormat = displayTimeFormat;
    }

    public int getHour(){
        return hour;
    }
    public void setHour(int hour) throws InvalidParameterException {
        if(hour >= 0 && hour <= 24)
            this.hour = hour;
        else
            throw new InvalidParameterException("Value of parameter (" + hour + "), is outside 0-24");
    }

    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) throws InvalidParameterException {
        if(hour >= 0 && hour <= 24)
            this.minute = minute;
        else
            throw new InvalidParameterException("Value of parameter (" + minute + "), is outside 0-60");
    }


    public TimeFormat getDisplayTimeFormat() {
        return displayTimeFormat;
    }
    public void setDisplayTimeFormat(TimeFormat displayTimeFormat) {
        this.displayTimeFormat = displayTimeFormat;
    }




    public boolean before(Time when) throws IllegalArgumentException {
        if(when == null)
            throw new IllegalArgumentException("Argument is null. Cannot compare");
        return this.getTimeInMinutes() < when.getTimeInMinutes();
    }

    public boolean after(Time when) throws IllegalArgumentException {
        if(when == null)
            throw new IllegalArgumentException("Argument is null. Cannot compare");
        return this.getTimeInMinutes() > when.getTimeInMinutes();
    }

    public int getTimeInMinutes() {
        return (hour*60) + minute;
    }
    public void setTimeInMinutes(int minutes) {
        if(minutes >= 0 && minutes <= 24*60) {
            this.hour = minutes/60;
            this.minute = minutes%60;
        }
        else
            throw new InvalidParameterException("Value of parameter (" + minute + "), is outside 0-60");
    }

    @Override
    public int compareTo(Time o) {
        int thisTime = this.getTimeInMinutes();
        int thatTime = o.getTimeInMinutes();
        return (thisTime<thatTime ? -1 : (thisTime==thatTime ? 0 : 1));
    }

    @Override
    public String toString() {
        switch (displayTimeFormat) {
            case FORMAT_24H:
                return String.format(Locale.getDefault(), "%1$02d:%2$02d", hour, minute);
            case FORMAT_12H:
                String amPm = "am";
                int hour12h = hour;
                if(hour > 12) {
                    amPm = "pm";
                    hour12h = hour - 12;
                }
                return String.format(Locale.getDefault(), "%1$02d:%2$02d %3$s", hour12h, minute, amPm);
            default:
                throw new IllegalArgumentException("displayTimeFormat is null. Cannot toString()");
        }
    }
}
