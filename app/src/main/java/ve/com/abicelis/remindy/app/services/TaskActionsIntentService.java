package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.exception.CouldNotUpdateDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.AlarmManagerUtil;
import ve.com.abicelis.remindy.util.CalendarUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;

/**
 * Created by abice on 31/5/2017.
 */

public class TaskActionsIntentService extends IntentService {

    public static final String ACTION_SET_TASK_DONE = "ACTION_SET_TASK_DONE";
    public static final String ACTION_POSTPONE_TASK = "ACTION_POSTPONE_TASK";
    public static final String PARAM_TASK_ID = "PARAM_TASK_ID";
    private static final int POSTPONE_MINUTES = 30;

    public TaskActionsIntentService() {
        super("TaskActionsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent == null || intent.getAction() == null)
            return;

        if(intent.getAction().equals(ACTION_SET_TASK_DONE)) {
            int taskId = intent.getIntExtra(PARAM_TASK_ID, -1);

            if(taskId != -1) {

                //Dismiss the notification
                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(taskId);

                RemindyDAO dao = new RemindyDAO(getApplicationContext());
                try{
                    Task task = dao.getTask(taskId);
                    task.setDoneDate(CalendarUtil.getNewInstanceZeroedCalendar());
                    task.setStatus(TaskStatus.DONE);
                    dao.updateTask(task);

                } catch (CouldNotGetDataException | CouldNotUpdateDataException e) {
                    Toast.makeText(this, getResources().getString(R.string.task_actions_service_could_not_set_done), Toast.LENGTH_SHORT).show();
                }

            }
        } else if (intent.getAction().equals(ACTION_POSTPONE_TASK)) {
            int taskId = intent.getIntExtra(PARAM_TASK_ID, -1);

            if(taskId != -1) {
                //Dismiss the notification
                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(taskId);

                RemindyDAO dao = new RemindyDAO(getApplicationContext());
                try {
                    Task task = dao.getTask(taskId);

                    Time time;
                    switch (task.getReminderType()) {
                        case REPEATING:
                            time = ((RepeatingReminder)task.getReminder()).getTime();
                            break;

                        case ONE_TIME:
                            time = ((OneTimeReminder)task.getReminder()).getTime();
                            break;

                        default:
                            return;    //TODO: Show some kind of error?
                    }


                    int timeInMinutes = time.getTimeInMinutes();
                    timeInMinutes += POSTPONE_MINUTES;
                    timeInMinutes = (timeInMinutes >= 1440 ? 1339 : timeInMinutes);  //Set max per day to 24:59:00
                    time.setTimeInMinutes(timeInMinutes);

                    dao.updateTask(task);

                    //Remove task from triggeredTasks list
                    SharedPreferenceUtil.removeIdFromTriggeredTasks(getApplicationContext(), task.getId());

                    //Update alarms
                    AlarmManagerUtil.updateAlarms(getApplicationContext());

                } catch (CouldNotGetDataException | CouldNotUpdateDataException e) {
                    Toast.makeText(this, getResources().getString(R.string.task_actions_service_could_not_set_done), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
