package ve.com.abicelis.remindy.util;

import java.util.Calendar;

import ve.com.abicelis.remindy.model.Time;

/**
 * Created by abice on 26/4/2017.
 */

public class CalendarUtil {

    public static Calendar getNewInstanceZeroedCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static void copyCalendar(Calendar copyFrom, Calendar copyTo) {
        if(copyFrom == null || copyTo == null)
            throw new NullPointerException("copyCalendar(), One of both parameters are null");

        copyTo.setTimeZone(copyFrom.getTimeZone());
        copyTo.setTimeInMillis(copyFrom.getTimeInMillis());
    }

    public static long getDifferenceMinutesBetween(Calendar a, Calendar b) {
        long differenceMinutes = ( a.getTimeInMillis() - b.getTimeInMillis() ) / 60000; //60 * 1000    toMinutes * toSeconds
        return differenceMinutes;
    }

    public static Calendar getCalendarFromDateAndTime(Calendar date, Time time) {
        if(date == null || time == null)
            throw new NullPointerException("getCalendarFromDateAndTime(), One of both parameters are null");

        Calendar cal = Calendar.getInstance();
        copyCalendar(date, cal);

        cal.set(Calendar.HOUR_OF_DAY, time.getHour());
        cal.set(Calendar.MINUTE, time.getMinute());

        return cal;
    }
}
