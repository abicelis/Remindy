package ve.com.abicelis.remindy.util;

import android.content.res.Resources;

import java.io.InvalidClassException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.TaskSortType;
import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 26/3/2017.
 */

public class TaskHeaderUtil {

    private CalendarPeriod periodLastYear;
    private CalendarPeriod periodLastMonth;
    private CalendarPeriod periodLastWeek;
    private CalendarPeriod periodYesterday;
    private CalendarPeriod periodOverdue;
    private CalendarPeriod periodToday;
    private CalendarPeriod periodTomorrow;
    private CalendarPeriod periodThisWeek;
    private CalendarPeriod periodNextWeek;
    private CalendarPeriod periodThisMonth;
    private CalendarPeriod periodNextMonth;
    private CalendarPeriod periodThisYear;
    private CalendarPeriod periodNextYear;

    private ArrayList<Task> tasksPast;
    private ArrayList<Task> tasksLastYear;
    private ArrayList<Task> tasksLastMonth;
    private ArrayList<Task> tasksLastWeek;
    private ArrayList<Task> tasksYesterday;
    private ArrayList<Task> tasksOverdue;
    private ArrayList<Task> tasksLocationBased;
    private ArrayList<Task> tasksToday;
    private ArrayList<Task> tasksTomorrow;
    private ArrayList<Task> tasksThisWeek;
    private ArrayList<Task> tasksNextWeek;
    private ArrayList<Task> tasksThisMonth;
    private ArrayList<Task> tasksNextMonth;
    private ArrayList<Task> tasksThisYear;
    private ArrayList<Task> tasksNextYear;
    private ArrayList<Task> tasksFuture;

    public TaskHeaderUtil () {
        periodLastYear = new CalendarPeriod(CalendarPeriodType.LAST_YEAR);
        periodLastMonth = new CalendarPeriod(CalendarPeriodType.LAST_MONTH);
        periodLastWeek = new CalendarPeriod(CalendarPeriodType.LAST_WEEK);
        periodYesterday = new CalendarPeriod(CalendarPeriodType.YESTERDAY);
        periodOverdue = new CalendarPeriod(CalendarPeriodType.OVERDUE);
        periodToday = new CalendarPeriod(CalendarPeriodType.TODAY);
        periodTomorrow = new CalendarPeriod(CalendarPeriodType.TOMORROW);
        periodThisWeek = new CalendarPeriod(CalendarPeriodType.THIS_WEEK);
        periodNextWeek = new CalendarPeriod(CalendarPeriodType.NEXT_WEEK);
        periodThisMonth = new CalendarPeriod(CalendarPeriodType.THIS_MONTH);
        periodNextMonth = new CalendarPeriod(CalendarPeriodType.NEXT_MONTH);
        periodThisYear = new CalendarPeriod(CalendarPeriodType.THIS_YEAR);
        periodNextYear = new CalendarPeriod(CalendarPeriodType.NEXT_YEAR);

        tasksPast = new ArrayList<>();
        tasksLastYear = new ArrayList<>();
        tasksLastMonth = new ArrayList<>();
        tasksLastWeek = new ArrayList<>();
        tasksYesterday = new ArrayList<>();
        tasksOverdue = new ArrayList<>();
        tasksLocationBased = new ArrayList<>();
        tasksToday = new ArrayList<>();
        tasksTomorrow = new ArrayList<>();
        tasksThisWeek = new ArrayList<>();
        tasksNextWeek = new ArrayList<>();
        tasksThisMonth = new ArrayList<>();
        tasksNextMonth = new ArrayList<>();
        tasksThisYear = new ArrayList<>();
        tasksNextYear = new ArrayList<>();
        tasksFuture = new ArrayList<>();
    }


    /* Programmed Tasks */
    public ArrayList<TaskViewModel> generateProgrammedTaskHeaderList(List<Task> tasks, TaskSortType sortType, Resources resources) throws InvalidClassException {
        ArrayList<TaskViewModel> result = new ArrayList<>();

        clearTaskBuckets();

        if(sortType == TaskSortType.DATE) {
            for (Task current : tasks) {

                if(current.getReminderType() == null)
                    throw new NullPointerException("NULL ReminderType passed into TaskHeaderUtil.generateProgrammedTaskHeaderList()");

                switch (current.getReminderType()) {
                    case NONE:
                        throw new InvalidClassException("Wrong ReminderType passed into TaskHeaderUtil.generateProgrammedTaskHeaderList()");
                    case LOCATION_BASED:
                        tasksLocationBased.add(current);
                        break;
                    case ONE_TIME:
                        insertProgrammedTaskIntoBucket(current, ((OneTimeReminder)current.getReminder()).getDate());
                        break;
                    case REPEATING:
                        //TODO: Not exactly correct.. must evaluate RepeatEndType... etc.
                        //insertProgrammedTaskIntoBucket(current, calculateRepeatingReminderEndDate((RepeatingReminder)current.getReminder()) );
                        insertProgrammedTaskIntoBucket(current, ((RepeatingReminder)current.getReminder()).getDate());
                        break;
                    default:
                        throw new InvalidParameterException("Unhandled ReminderType passed into TaskHeaderUtil.generateProgrammedTaskHeaderList()");
                }
            }

            if(tasksOverdue.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_overdue), true));
                dumpTaskBucketIntoViewModelList(tasksOverdue, result);
            }

            if(tasksLocationBased.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_location_based), false));
                dumpTaskBucketIntoViewModelList(tasksLocationBased, result);
            }

            if(tasksToday.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_today), false));
                dumpTaskBucketIntoViewModelList(tasksToday, result);
            }

            if(tasksTomorrow.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_tomorrow), false));
                dumpTaskBucketIntoViewModelList(tasksTomorrow, result);
            }

            if(tasksThisWeek.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_week), false));
                dumpTaskBucketIntoViewModelList(tasksThisWeek, result);
            }
            if(tasksNextWeek.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_next_week), false));
                dumpTaskBucketIntoViewModelList(tasksNextWeek, result);
            }

            if(tasksThisMonth.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_month), false));
                dumpTaskBucketIntoViewModelList(tasksThisMonth, result);
            }

            if(tasksNextMonth.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_next_month), false));
                dumpTaskBucketIntoViewModelList(tasksNextMonth, result);
            }

            if(tasksThisYear.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_year), false));
                dumpTaskBucketIntoViewModelList(tasksThisYear, result);
            }

            if(tasksNextYear.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_next_year), false));
                dumpTaskBucketIntoViewModelList(tasksNextYear, result);
            }

            if(tasksFuture.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_future), false));
                dumpTaskBucketIntoViewModelList(tasksFuture, result);
            }

        }

        return result;
    }

    private void insertProgrammedTaskIntoBucket(Task task, Calendar taskDate) {
        if(periodOverdue.isInPeriod(taskDate))
            tasksOverdue.add(task);

        else if (periodToday.isInPeriod(taskDate))
            tasksToday.add(task);

        else if (periodTomorrow.isInPeriod(taskDate))
            tasksTomorrow.add(task);

        else if (periodThisWeek.isInPeriod(taskDate))
            tasksThisWeek.add(task);

        else if (periodNextWeek.isInPeriod(taskDate))
            tasksNextWeek.add(task);

        else if (periodThisMonth.isInPeriod(taskDate))
            tasksNextMonth.add(task);

        else if (periodNextMonth.isInPeriod(taskDate))
            tasksNextMonth.add(task);

        else if (periodThisYear.isInPeriod(taskDate))
            tasksThisYear.add(task);

        else if (periodNextYear.isInPeriod(taskDate))
            tasksNextYear.add(task);

        else
            tasksFuture.add(task);
    }




    /* Done Tasks */
    public ArrayList<TaskViewModel> generateDoneTaskHeaderList(List<Task> tasks, TaskSortType sortType, Resources resources) throws InvalidClassException {
        ArrayList<TaskViewModel> result = new ArrayList<>();

        clearTaskBuckets();

        if(sortType == TaskSortType.DATE) {
            for (Task current : tasks) {

                if(current.getReminderType() == null)
                    throw new NullPointerException("NULL ReminderType passed into TaskHeaderUtil.generateProgrammedTaskHeaderList()");

                insertDoneTaskIntoBucket(current, current.getDoneDate());
            }

            if(tasksToday.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_today), false));
                dumpTaskBucketIntoViewModelList(tasksToday, result);
            }

            if(tasksYesterday.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_yesterday), false));
                dumpTaskBucketIntoViewModelList(tasksYesterday, result);
            }

            if(tasksThisWeek.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_week), false));
                dumpTaskBucketIntoViewModelList(tasksThisWeek, result);
            }
            if(tasksLastWeek.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_last_week), false));
                dumpTaskBucketIntoViewModelList(tasksLastWeek, result);
            }

            if(tasksThisMonth.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_month), false));
                dumpTaskBucketIntoViewModelList(tasksThisMonth, result);
            }

            if(tasksLastMonth.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_last_month), false));
                dumpTaskBucketIntoViewModelList(tasksLastMonth, result);
            }

            if(tasksThisYear.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_this_year), false));
                dumpTaskBucketIntoViewModelList(tasksThisYear, result);
            }

            if(tasksLastYear.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_last_year), false));
                dumpTaskBucketIntoViewModelList(tasksLastYear, result);
            }

            if(tasksPast.size() > 0) {
                result.add(new TaskViewModel(resources.getString(R.string.task_header_past), false));
                dumpTaskBucketIntoViewModelList(tasksPast, result);
            }

        }

        return result;
    }

    private void insertDoneTaskIntoBucket(Task task, Calendar taskDate) {
        if (periodToday.isInPeriod(taskDate))
            tasksToday.add(task);

        else if (periodYesterday.isInPeriod(taskDate))
            tasksYesterday.add(task);

        else if (periodThisWeek.isInPeriod(taskDate))
            tasksThisWeek.add(task);

        else if (periodLastWeek.isInPeriod(taskDate))
            tasksLastWeek.add(task);

        else if (periodThisMonth.isInPeriod(taskDate))
            tasksNextMonth.add(task);

        else if (periodLastMonth.isInPeriod(taskDate))
            tasksLastMonth.add(task);

        else if (periodThisYear.isInPeriod(taskDate))
            tasksThisYear.add(task);

        else if (periodLastYear.isInPeriod(taskDate))
            tasksLastYear.add(task);
        else
            tasksPast.add(task);
    }





    private void clearTaskBuckets() {
        tasksLastYear.clear();
        tasksLastMonth.clear();
        tasksLastWeek.clear();
        tasksYesterday.clear();
        tasksOverdue.clear();
        tasksLocationBased.clear();
        tasksToday.clear();
        tasksTomorrow.clear();
        tasksThisWeek.clear();
        tasksNextWeek.clear();
        tasksThisMonth.clear();
        tasksNextMonth.clear();
        tasksThisYear.clear();
        tasksNextYear.clear();
        tasksFuture.clear();
    }

    private void dumpTaskBucketIntoViewModelList(List<Task> tasks, List<TaskViewModel> result) {
        for (Task task: tasks) {
            switch (task.getReminderType()) {
                case NONE:
                    result.add(new TaskViewModel(task, TaskViewModelType.UNPROGRAMMED_REMINDER));
                    break;

                case ONE_TIME:
                    result.add(new TaskViewModel(task, TaskViewModelType.ONE_TIME_REMINDER));
                    break;

                case REPEATING:
                    result.add(new TaskViewModel(task, TaskViewModelType.REPEATING_REMINDER));
                    break;

                case LOCATION_BASED:
                    result.add(new TaskViewModel(task, TaskViewModelType.LOCATION_BASED_REMINDER));
                    break;

                default:
                    throw new InvalidParameterException("Unhandled ReminderType passed into TaskHeaderUtil.dumpTaskBucketIntoViewModelList()");
            }
        }

    }

    private void copyCalendar(Calendar copyFrom, Calendar copyTo) {
        if(copyFrom == null || copyTo == null)
            throw new NullPointerException("One of both parameters are null");

        copyTo.setTimeZone(copyFrom.getTimeZone());
        copyTo.setTimeInMillis(copyFrom.getTimeInMillis());
    }




    private class CalendarPeriod {
        private Calendar start;
        private Calendar end;

        CalendarPeriod(CalendarPeriodType periodType) {

            start = Calendar.getInstance();
            start.add(Calendar.DAY_OF_MONTH, 0);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 999);

            switch (periodType) {
                case LAST_YEAR:
                    start.add(Calendar.YEAR, -1);
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    copyCalendar(start, end);
                    end.add(Calendar.YEAR, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case LAST_MONTH:
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    start.add(Calendar.MONTH, -1);
                    copyCalendar(start, end);
                    end.add(Calendar.MONTH, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case LAST_WEEK:
                    start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                    start.add(Calendar.WEEK_OF_MONTH, -1);
                    copyCalendar(start, end);
                    end.add(Calendar.DAY_OF_MONTH, 7);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case YESTERDAY:
                    start.add(Calendar.DAY_OF_MONTH, -1);
                    end.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case OVERDUE:
                    start.set(Calendar.YEAR, 1);    //TODO: Set to way in the past, figure out how to set Calendar.MINDATE
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    end.add(Calendar.DAY_OF_MONTH, -1); //End of Yesterday
                    break;
                case TODAY:
                    //Dates are already set
                    break;
                case TOMORROW:
                    start.add(Calendar.DAY_OF_MONTH, 1);
                    end.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case THIS_WEEK:
                    start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                    copyCalendar(start, end);
                    end.add(Calendar.DAY_OF_MONTH, 7);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case NEXT_WEEK:
                    start.add(Calendar.WEEK_OF_YEAR, 1);
                    start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                    copyCalendar(start, end);
                    end.add(Calendar.DAY_OF_MONTH, 7);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case THIS_MONTH:
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    copyCalendar(start, end);
                    end.add(Calendar.MONTH, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case NEXT_MONTH:
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    start.add(Calendar.MONTH, 1);
                    copyCalendar(start, end);
                    end.add(Calendar.MONTH, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case THIS_YEAR:
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    copyCalendar(start, end);
                    end.add(Calendar.YEAR, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
                case NEXT_YEAR:
                    start.add(Calendar.YEAR, 1);
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    copyCalendar(start, end);
                    end.add(Calendar.YEAR, 1);
                    end.add(Calendar.MILLISECOND, -1);
                    break;
            }
        }

        public boolean isInPeriod(Calendar date) {
            return (date.compareTo(start) >= 0 && date.compareTo(end) <= 0);
        }
    }

    private enum CalendarPeriodType {
        OVERDUE, TODAY, TOMORROW, THIS_WEEK, NEXT_WEEK, THIS_MONTH, NEXT_MONTH, THIS_YEAR, NEXT_YEAR,
        YESTERDAY, LAST_WEEK, LAST_MONTH, LAST_YEAR
    }
}
