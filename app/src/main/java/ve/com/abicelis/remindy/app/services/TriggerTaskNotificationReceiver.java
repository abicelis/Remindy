package ve.com.abicelis.remindy.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.AlarmManagerUtil;
import ve.com.abicelis.remindy.util.CalendarUtil;
import ve.com.abicelis.remindy.util.NotificationUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.TaskUtil;
import ve.com.abicelis.remindy.viewmodel.TaskTriggerViewModel;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 29/4/2017.
 */

public class TriggerTaskNotificationReceiver extends BroadcastReceiver {

    //CONSTS
    private static final String TAG = TriggerTaskNotificationReceiver.class.getSimpleName();
    public static final String TASK_EXTRA = "TASK_EXTRA";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "TriggerTaskNotificationReceiver");

        //Get TASK_EXTRA
        TaskTriggerViewModel task;
        try {
            task = (TaskTriggerViewModel)intent.getSerializableExtra(TASK_EXTRA);
        } catch (Exception e) {
            task = null;
        }

        if(task != null) {
            Log.d(TAG, "Triggering task ID " + task.getTask().getId());

            String contentTitle = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_title), task.getTask().getTitle());
            int triggerMinutesBeforeNotification = SharedPreferenceUtil.getTriggerMinutesBeforeNotification(context).getMinutes();
            String contentText = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_text), triggerMinutesBeforeNotification);

            NotificationUtil.displayNotification(context, task.getTask().getId(), contentTitle, contentText);

            //Add task to triggeredTasks list
            List<Integer> triggeredTasks = SharedPreferenceUtil.getTriggeredTaskList(context);
            triggeredTasks.add(task.getTask().getId());
            SharedPreferenceUtil.setTriggeredTaskList(triggeredTasks, context);

        } else {
            Log.d(TAG, "TriggerTaskNotificationReceiver triggered with no TASK_EXTRA!");
        }

        //Set next alarm
        AlarmManagerUtil.updateAlarms(context);
    }
}
