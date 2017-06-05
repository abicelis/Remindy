package ve.com.abicelis.remindy.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.AlarmManagerUtil;
import ve.com.abicelis.remindy.util.NotificationUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.TaskUtil;
import ve.com.abicelis.remindy.viewmodel.TaskTriggerViewModel;

/**
 * Created by abice on 29/4/2017.
 */

public class TriggerTaskNotificationReceiver extends BroadcastReceiver {

    //CONSTS
    private static final String TAG = TriggerTaskNotificationReceiver.class.getSimpleName();
    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "TriggerTaskNotificationReceiver");

        //Get TASK_ID_EXTRA
        int taskId;
        try {
            taskId = intent.getIntExtra(TASK_ID_EXTRA, -1);
        } catch (Exception e) {
            taskId = -1;
        }

        if(taskId != -1) {
            Task task;
            try {
                task = new RemindyDAO(context).getTask(taskId);
            } catch (CouldNotGetDataException e) {
               //TODO: Show some kind of error here
                return;
            }

            if(task != null) {
                Log.d(TAG, "Triggering task ID " + taskId);

                String triggerTime;
                switch (task.getReminderType()) {
                    case ONE_TIME:
                        triggerTime = ((OneTimeReminder)task.getReminder()).getTime().toString();
                        break;
                    case REPEATING:
                        triggerTime = ((RepeatingReminder)task.getReminder()).getTime().toString();
                        break;
                    default:
                        //TODO: Show some kind of error here
                        return;
                }

                String contentTitle = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_title), task.getTitle());
                int triggerMinutesBeforeNotification = SharedPreferenceUtil.getTriggerMinutesBeforeNotification(context).getMinutes();
                String contentText = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_text), triggerMinutesBeforeNotification, triggerTime);

                NotificationUtil.displayNotification(context, task, contentTitle, contentText);

                //Add task to triggeredTasks list
                List<Integer> triggeredTasks = SharedPreferenceUtil.getTriggeredTaskList(context);
                triggeredTasks.add(task.getId());
                SharedPreferenceUtil.setTriggeredTaskList(triggeredTasks, context);
            }



        } else {
            Log.d(TAG, "TriggerTaskNotificationReceiver triggered with no TASK_ID_EXTRA!");
        }

        //Set next alarm
        AlarmManagerUtil.updateAlarms(context);
    }
}
