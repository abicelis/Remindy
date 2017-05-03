package ve.com.abicelis.remindy.util;

import java.security.InvalidParameterException;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderType;
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

        if(reminder.getType().equals(ReminderType.LOCATION_BASED))
            return false;

        if(reminder.getType().equals(ReminderType.REPEATING)) {
            return ( getRepeatingReminderNextCalendar((RepeatingReminder) reminder) == null );
        }

        //For OneTime reminders
        CalendarPeriod overdueCP = new CalendarPeriod(CalendarPeriodType.OVERDUE);
        Calendar endDate = getReminderEndCalendar(reminder);
        return overdueCP.isInPeriod(endDate);
    }


    public static Calendar getReminderEndCalendar(Reminder reminder) {
        switch (reminder.getType()) {
            case NONE:
            case LOCATION_BASED:
                return null;

            case ONE_TIME:
                return CalendarUtil.getCalendarFromDateAndTime( ((OneTimeReminder)reminder).getDate(), ((OneTimeReminder)reminder).getTime() );

            case REPEATING:
                return getRepeatingReminderEndCalendar(((RepeatingReminder)reminder));

            default:
                throw new InvalidParameterException("Invalid ReminderType param on TaskUtil.getReminderEndCalendar()");
        }
    }


    public static Calendar getRepeatingReminderEndCalendar(RepeatingReminder repeatingReminder) {
        Calendar cal = Calendar.getInstance();

        switch (repeatingReminder.getRepeatEndType()) {
            case FOREVER:
                //TODO: Need to return Date.MAX or so
                cal.add(Calendar.YEAR, 100);
                break;

            case UNTIL_DATE:
                cal = CalendarUtil.getCalendarFromDateAndTime( repeatingReminder.getRepeatEndDate(), repeatingReminder.getTime());
                break;

            case FOR_X_EVENTS:
                cal = CalendarUtil.getCalendarFromDateAndTime( repeatingReminder.getDate(), repeatingReminder.getTime());
                int dateField = getDateFieldFromRepeatType(repeatingReminder.getRepeatType());

                for (int i = 0; i < repeatingReminder.getRepeatEndNumberOfEvents(); i++)
                    cal.add(dateField, repeatingReminder.getRepeatInterval());
                break;
        }
        return cal;
    }


    public static Calendar getRepeatingReminderNextCalendar(RepeatingReminder repeatingReminder) {

        Calendar today = Calendar.getInstance();
        Calendar endDate = getRepeatingReminderEndCalendar(repeatingReminder);
        Calendar cal = CalendarUtil.getCalendarFromDateAndTime( repeatingReminder.getDate(), repeatingReminder.getTime());

        //TODO: Cant use getDateFieldFromRepeatType(), Gives off a weird warning
        //final int dateField = getDateFieldFromRepeatType(repeatingReminder.getRepeatType());


        while(true) {
            if (cal.compareTo(endDate) >= 0)    //If cal passed endDate, reminder is overdue, return null
                return null;
            if(cal.compareTo(today) >= 0) {
                return cal;
            }

            //TODO: Cant use getDateFieldFromRepeatType(), Gives off a weird warning
            //cal.add(dateField, repeatingReminder.getRepeatInterval()); break;
            switch (repeatingReminder.getRepeatType()) {
                case DAILY: cal.add(Calendar.DAY_OF_WEEK, repeatingReminder.getRepeatInterval()); break;
                case WEEKLY: cal.add(Calendar.WEEK_OF_YEAR, repeatingReminder.getRepeatInterval()); break;
                case MONTHLY: cal.add(Calendar.MONTH, repeatingReminder.getRepeatInterval()); break;
                case YEARLY: cal.add(Calendar.YEAR, repeatingReminder.getRepeatInterval()); break;
                default: throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndCalendar()");
            }
        }
    }

    private static int getDateFieldFromRepeatType(ReminderRepeatType repeatType) {

        switch (repeatType) {
            case DAILY: return Calendar.DAY_OF_MONTH;
            case WEEKLY: return Calendar.WEEK_OF_YEAR;
            case MONTHLY: return Calendar.MONTH;
            case YEARLY: return Calendar.YEAR;
            default: throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndCalendar()");
        }
    }


}
