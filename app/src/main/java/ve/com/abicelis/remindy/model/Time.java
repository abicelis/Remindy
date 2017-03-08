package ve.com.abicelis.remindy.model;

import java.util.Locale;

import ve.com.abicelis.remindy.enums.TimeFormat;

/**
 * Created by abice on 8/3/2017.
 */

public class Time implements Comparable<Time> {
    private int hour;
    private int minute;
    private TimeFormat format = TimeFormat.FORMAT_24H;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        format = TimeFormat.FORMAT_24H;
    }

    public Time(int hour, int minute, TimeFormat format) {
        this(hour, minute);
        this.format = format;
    }

    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        if(hour < 0 || hour > 24)
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }


    public TimeFormat getFormat() {
        return format;
    }
    public void setFormat(TimeFormat format) {
        this.format = format;
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

    private int getTimeInMinutes() {
        return (hour*60) + minute;
    }

    @Override
    public int compareTo(Time o) {
        if(o == null)
            throw new IllegalArgumentException("Argument is null. Cannot compare");
        int thisTime = this.getTimeInMinutes();
        int thatTime = o.getTimeInMinutes();
        return (thisTime<thatTime ? -1 : (thisTime==thatTime ? 0 : 1));
    }

    @Override
    public String toString() {
        switch (format) {
            case FORMAT_24H:
                return String.format(Locale.getDefault(), "%1$01d:%2$01d", hour, minute);
            case FORMAT_12H:
                String amPm = "am";
                int hour12h = hour;
                if(hour > 12) {
                    amPm = "pm";
                    hour12h = hour - 12;
                }
                return String.format(Locale.getDefault(), "%1$01d:%2$01d %3$s", hour12h, minute, amPm);
            default:
                throw new IllegalArgumentException("Argument is null. Cannot toString()");
        }
    }
}
