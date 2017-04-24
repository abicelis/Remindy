package ve.com.abicelis.remindy.model;

import java.util.Calendar;
import java.util.Comparator;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.TaskUtil;

/**
 * Created by abice on 11/3/2017.
 */

public class TasksByReminderDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getReminder() == null || (o1.getReminderType() != ReminderType.ONE_TIME && o1.getReminderType() != ReminderType.REPEATING))
            return 1;
        if(o2.getReminder() == null || (o2.getReminderType() != ReminderType.ONE_TIME && o2.getReminderType() != ReminderType.REPEATING))
            return -1;


        Calendar o1Date = null;
        if(o1.getReminderType() == ReminderType.ONE_TIME)
            o1Date = ((OneTimeReminder) o1.getReminder()).getDate();
        else {  //Repeating reminder
            Calendar cal = TaskUtil.getRepeatingReminderNextDate(((RepeatingReminder) o1.getReminder()));
            o1Date = (cal != null ? cal : TaskUtil.getRepeatingReminderEndDate(((RepeatingReminder) o1.getReminder())));
        }

        Calendar o2Date = null;
        if(o2.getReminderType() == ReminderType.ONE_TIME)
            o2Date = ((OneTimeReminder) o2.getReminder()).getDate();
        else {  //Repeating reminder
            Calendar cal = TaskUtil.getRepeatingReminderNextDate(((RepeatingReminder) o2.getReminder()));
            o1Date = (cal != null ? cal : TaskUtil.getRepeatingReminderEndDate(((RepeatingReminder) o2.getReminder())));
        }

        //Calendar o1Date = (o1.getReminderType() == ReminderType.ONE_TIME ? ((OneTimeReminder) o1.getReminder()).getDate() : TaskUtil.getRepeatingReminderNextDate(((RepeatingReminder) o1.getReminder())) );
        //Calendar o2Date = (o2.getReminderType() == ReminderType.ONE_TIME ? ((OneTimeReminder) o2.getReminder()).getDate() : TaskUtil.getRepeatingReminderNextDate(((RepeatingReminder) o2.getReminder())) );
        return o1Date.compareTo(o2Date);
    }
}
