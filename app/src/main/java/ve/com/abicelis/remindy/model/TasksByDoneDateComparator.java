package ve.com.abicelis.remindy.model;

import java.util.Calendar;
import java.util.Comparator;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Task;

/**
 * Created by abice on 11/3/2017.
 */

public class TasksByDoneDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getDoneDate() == null)
            return 1;
        if(o2.getDoneDate() == null )
            return -1;

        return o1.getDoneDate().compareTo(o2.getDoneDate());
    }
}
