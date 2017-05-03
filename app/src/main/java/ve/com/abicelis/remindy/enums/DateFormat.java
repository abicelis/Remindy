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
                SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                String str = formatter.format(calendar.getTime());

                if(Locale.getDefault().equals(Locale.ENGLISH) || Locale.getDefault().equals(Locale.US) || Locale.getDefault().equals(Locale.UK) || Locale.getDefault().equals(Locale.CANADA))
                    str = str.replaceFirst(",", DateFormat.getDayNumberSuffix(calendar.get(Calendar.DAY_OF_MONTH)) + "," );

                return str;
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

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

}
