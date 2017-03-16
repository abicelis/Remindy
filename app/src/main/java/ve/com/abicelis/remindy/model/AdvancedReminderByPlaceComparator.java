package ve.com.abicelis.remindy.model;

import java.util.Comparator;

/**
 * Created by abice on 11/3/2017.
 */

public class AdvancedReminderByPlaceComparator implements Comparator<AdvancedReminder> {
    @Override
    public int compare(AdvancedReminder o1, AdvancedReminder o2) {
        if(o1.getPlace() == null)
            return -1;
        if(o2.getPlace() == null)
            return 1;
        return o1.getPlace().getAlias().compareTo(o2.getPlace().getAlias());
    }
}
