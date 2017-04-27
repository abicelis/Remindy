package ve.com.abicelis.remindy.util;

import java.util.Calendar;

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
}
