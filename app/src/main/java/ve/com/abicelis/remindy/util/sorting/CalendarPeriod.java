package ve.com.abicelis.remindy.util.sorting;

import java.util.Calendar;

import ve.com.abicelis.remindy.util.CalendarUtil;

/**
 * Created by abice on 22/4/2017.
 */

public class CalendarPeriod {
    private Calendar start;
    private Calendar end;

    public CalendarPeriod(CalendarPeriodType periodType) {

        start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, 0);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        switch (periodType) {
            case LAST_YEAR:
                start.add(Calendar.YEAR, -1);
                start.set(Calendar.MONTH, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.YEAR, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case LAST_MONTH:
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.add(Calendar.MONTH, -1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case LAST_WEEK:
                start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                start.add(Calendar.WEEK_OF_MONTH, -1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.DAY_OF_MONTH, 7);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case YESTERDAY:
                start.add(Calendar.DAY_OF_MONTH, -1);
                end.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case OVERDUE:
                start.set(Calendar.YEAR, 1);    //TODO: Set to way in the past, figure out how to set Calendar.MINDATE
                start.set(Calendar.MONTH, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                end.add(Calendar.DAY_OF_MONTH, -1); //End of Yesterday
                break;
            case TODAY:
                //Dates are already set
                break;
            case TOMORROW:
                start.add(Calendar.DAY_OF_MONTH, 1);
                end.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case THIS_WEEK:
                start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.DAY_OF_MONTH, 7);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case NEXT_WEEK:
                start.add(Calendar.WEEK_OF_YEAR, 1);
                start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.DAY_OF_MONTH, 7);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case THIS_MONTH:
                start.set(Calendar.DAY_OF_MONTH, 1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case NEXT_MONTH:
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.add(Calendar.MONTH, 1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case THIS_YEAR:
                start.set(Calendar.MONTH, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.YEAR, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
            case NEXT_YEAR:
                start.add(Calendar.YEAR, 1);
                start.set(Calendar.MONTH, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                CalendarUtil.copyCalendar(start, end);
                end.add(Calendar.YEAR, 1);
                end.add(Calendar.MILLISECOND, -1);
                break;
        }
    }

    public boolean isInPeriod(Calendar date) {
        return (date.compareTo(start) >= 0 && date.compareTo(end) <= 0);
    }
}