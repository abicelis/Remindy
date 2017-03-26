package ve.com.abicelis.remindy.model;

import java.util.Comparator;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;

/**
 * Created by abice on 11/3/2017.
 */

public class TasksByPlaceComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getReminder() == null || o1.getReminderType() != ReminderType.LOCATION_BASED)
            return 1;
        if(o2.getReminder() == null || o2.getReminderType() != ReminderType.LOCATION_BASED)
            return -1;

        LocationBasedReminder o1reminder = (LocationBasedReminder) o1.getReminder();
        LocationBasedReminder o2reminder = (LocationBasedReminder) o2.getReminder();

        return o1reminder.getPlace().getAlias().compareTo(o2reminder.getPlace().getAlias());
    }
}
