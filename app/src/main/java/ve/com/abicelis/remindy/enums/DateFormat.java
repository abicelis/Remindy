package ve.com.abicelis.remindy.enums;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by abice on 3/3/2017.
 */

public enum DateFormat {
        PRETTY_DATE {
            @Override
            public String formatCalendar(Calendar calendar) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return formatter.format(calendar.getTime());
            }
        },
        MONTH_DAY_YEAR{
            @Override
            public String formatCalendar(Calendar calendar) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                return formatter.format(calendar.getTime());
            }
        },
        DAY_MONTH_YEAR{
            @Override
            public String formatCalendar(Calendar calendar) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return formatter.format(calendar.getTime());
            }
        },
    ;

    public abstract String formatCalendar(Calendar calendar);
}
