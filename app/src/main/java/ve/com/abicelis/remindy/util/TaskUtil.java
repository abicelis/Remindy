package ve.com.abicelis.remindy.util;

import java.security.InvalidParameterException;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.Reminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.sorting.CalendarPeriod;
import ve.com.abicelis.remindy.util.sorting.CalendarPeriodType;

/**
 * Created by abice on 22/4/2017.
 */

public class TaskUtil {


    public static boolean checkIfOverdue(Reminder reminder) {
        if(reminder == null)
            return false;

        CalendarPeriod overdueCP = new CalendarPeriod(CalendarPeriodType.OVERDUE);
        Calendar endDate = getReminderEndDate(reminder);
        if(endDate == null)
            return false;
        return overdueCP.isInPeriod(endDate);
    }


    public static Calendar getReminderEndDate(Reminder reminder) {
        switch (reminder.getType()) {
            case NONE:
            case LOCATION_BASED:
                return null;

            case ONE_TIME:
                return ((OneTimeReminder)reminder).getDate();

            case REPEATING:
                return getRepeatingReminderEndDate(((RepeatingReminder)reminder));

            default:
                throw new InvalidParameterException("Invalid ReminderType param on TaskUtil.getReminderEndDate()");
        }
    }


    public static Calendar getRepeatingReminderEndDate(RepeatingReminder repeatingReminder) {
        Calendar cal = Calendar.getInstance();

        switch (repeatingReminder.getRepeatEndType()) {
            case FOREVER:
                //TODO: Need to return Date.MAX or so
                cal.add(Calendar.YEAR, 100);
                break;

            case UNTIL_DATE:
                CalendarUtil.copyCalendar(repeatingReminder.getRepeatEndDate(), cal);
                break;

            case FOR_X_EVENTS:
                CalendarUtil.copyCalendar(repeatingReminder.getDate(), cal);
                int dateField = getDateFieldFromRepeatType(repeatingReminder.getRepeatType());

                for (int i = 0; i < repeatingReminder.getRepeatEndNumberOfEvents(); i++)
                    cal.add(dateField, repeatingReminder.getRepeatInterval());
                break;
        }
        return cal;
    }


    public static Calendar getRepeatingReminderNextDate(RepeatingReminder repeatingReminder) {

        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        CalendarUtil.copyCalendar(repeatingReminder.getDate(), cal);

        //TODO: Cant use getDateFieldFromRepeatType(), Gives off a weird warning
        //final int dateField = getDateFieldFromRepeatType(repeatingReminder.getRepeatType());

        if(checkIfOverdue(repeatingReminder))
            return null;
        while(true) {
            if(cal.compareTo(today) >= 0)
                return cal;

            //TODO: Cant use getDateFieldFromRepeatType(), Gives off a weird warning
            //cal.add(dateField, repeatingReminder.getRepeatInterval()); break;
            switch (repeatingReminder.getRepeatType()) {
                case DAILY: cal.add(Calendar.DAY_OF_WEEK, repeatingReminder.getRepeatInterval()); break;
                case WEEKLY: cal.add(Calendar.WEEK_OF_YEAR, repeatingReminder.getRepeatInterval()); break;
                case MONTHLY: cal.add(Calendar.MONTH, repeatingReminder.getRepeatInterval()); break;
                case YEARLY: cal.add(Calendar.YEAR, repeatingReminder.getRepeatInterval()); break;
                default: throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndDate()");
            }

        }
    }


    private static int getDateFieldFromRepeatType(ReminderRepeatType repeatType) {

        switch (repeatType) {
            case DAILY: return Calendar.DAY_OF_MONTH;
            case WEEKLY: return Calendar.WEEK_OF_YEAR;
            case MONTHLY: return Calendar.MONTH;
            case YEARLY: return Calendar.YEAR;
            default: throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndDate()");
        }
    }


}
