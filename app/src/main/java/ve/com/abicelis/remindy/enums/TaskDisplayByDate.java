package ve.com.abicelis.remindy.enums;

import java.io.Serializable;

/**
 * Created by abice on 3/3/2017.
 */

public enum TaskDisplayByDate implements Serializable {

    HEADER,
    ACTIVE,
    TODAY,
    TOMORROW,
    THIS_WEEK,
    NEXT_WEEK,
    THIS_MONTH,
    NEXT_MONTH,
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER

    /*
     *   - DONE. Tasks completed by user (doneDate != null)
     *   - ACTIVE:
     *          Tasks with TaskStatus.PROGRAMMED, and will trigger sometime in the present or in the future
     *          Basically, all Reminders which are *NOT* OVERDUE (See below)
     *   - OVERDUE:
     *          These are reminders which have TaskStatus.PROGRAMMED, but will never trigger because of their set dates
     *          Examples:
     *              1. Task has One-Time reminder, which is set in the past.
     *              2. Task has Repeating reminder and:
     *                  a. Its ReminderRepeatEndType=UNTIL_DATE, which is set in the past
     *                  b. Its ReminderRepeatEndType=FOR_X_EVENTS AND all events have already passed.
     */
}
