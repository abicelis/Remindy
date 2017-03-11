package ve.com.abicelis.remindy.model;

import java.util.Comparator;

/**
 * Created by abice on 11/3/2017.
 */

public class ReminderByPlaceComparator implements Comparator<Reminder> {
    @Override
    public int compare(Reminder o1, Reminder o2) {
        if(o1.getPlace() == null)
            return -1;
        if(o2.getPlace() == null)
            return 1;
        return o1.getPlace().getAlias().compareTo(o2.getPlace().getAlias());
    }
}
