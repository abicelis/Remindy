package ve.com.abicelis.remindy.model;

import java.security.InvalidParameterException;
import java.util.BitSet;
import java.util.Calendar;

/**
 * Created by abice on 16/3/2017.
 */

public class Weekdays {

    private BitSet value;

    public Weekdays() {
        value = new BitSet(7);
    }
    public Weekdays(int intValue) {
        this();
        setIntValue(intValue);
    }
    public Weekdays(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        this();

        value.set(0, monday);
        value.set(1, tuesday);
        value.set(2, wednesday);
        value.set(3, thursday);
        value.set(4, friday);
        value.set(5, saturday);
        value.set(6, sunday);
    }

    public void setDay(int calendarDay, boolean active) {
        switch (calendarDay) {
            case Calendar.MONDAY:
                value.set(0, active);
                break;
            case Calendar.TUESDAY:
                value.set(1, active);
                break;
            case Calendar.WEDNESDAY:
                value.set(2, active);
                break;
            case Calendar.THURSDAY:
                value.set(3, active);
                break;
            case Calendar.FRIDAY:
                value.set(4, active);
                break;
            case Calendar.SATURDAY:
                value.set(5, active);
                break;
            case Calendar.SUNDAY:
                value.set(6, active);
                break;
        }
    }

    public boolean getDay(int calendarDay) {
        switch (calendarDay) {
            case Calendar.MONDAY:
                return value.get(0);
            case Calendar.TUESDAY:
                return value.get(1);
            case Calendar.WEDNESDAY:
                return value.get(2);
            case Calendar.THURSDAY:
                return value.get(3);
            case Calendar.FRIDAY:
                return value.get(4);
            case Calendar.SATURDAY:
                return value.get(5);
            case Calendar.SUNDAY:
                return value.get(6);
            default:
                throw new InvalidParameterException("Invalid parameter. Must be a Calendar.DAY_OF_WEEK. Given value=" + calendarDay);
        }
    }


    public void setIntValue(int intValue) {

        if(intValue < 0)
            throw new InvalidParameterException("Invalid negative intValue");
        if(intValue > 127)
            throw new InvalidParameterException("Invalid intValue > 127");

        value.clear();
        for(int i = 6; i >= 0; i--) {
            int divisor  = (int)Math.pow(2, i);

            if(intValue >= divisor){
                value.set(i, true);
                intValue = intValue % divisor;
            }
        }
        boolean a = value.get(3);

    }

    public int getIntValue() {
        int previousBit = 0;
        int bit;
        int intValue = 0;
        while((bit = value.nextSetBit(previousBit)) != -1) {
            intValue |= (1 << bit);
            previousBit = bit+1;
        }
        return intValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((getDay(Calendar.MONDAY) ? "MONDAY" : ""));
        sb.append((getDay(Calendar.TUESDAY) ? " TUESDAY" : ""));
        sb.append((getDay(Calendar.WEDNESDAY) ? " WEDNESDAY" : ""));
        sb.append((getDay(Calendar.THURSDAY) ? " THURSDAY" : ""));
        sb.append((getDay(Calendar.FRIDAY) ? " FRIDAY" : ""));
        sb.append((getDay(Calendar.SATURDAY) ? " SATURDAY" : ""));
        sb.append((getDay(Calendar.SUNDAY) ? " SUNDAY" : ""));
        return sb.toString();
    }
}
