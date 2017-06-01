package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.exception.CouldNotUpdateDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.CalendarUtil;

/**
 * Created by abice on 31/5/2017.
 */

public class TaskActionsIntentService extends IntentService {

    public static final String ACTION_SET_TASK_DONE = "ACTION_SET_TASK_DONE";
    public static final String PARAM_TASK_ID = "PARAM_TASK_ID";

    public TaskActionsIntentService() {
        super("TaskActionsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent.getAction().equals(ACTION_SET_TASK_DONE)) {

            int taskId = intent.getIntExtra(PARAM_TASK_ID, -1);

            if(taskId != -1) {
                RemindyDAO dao = new RemindyDAO(getApplicationContext());

                try{
                    Task task = dao.getTask(taskId);
                    task.setDoneDate(CalendarUtil.getNewInstanceZeroedCalendar());
                    task.setStatus(TaskStatus.DONE);
                    dao.updateTask(task);

                } catch (CouldNotGetDataException | CouldNotUpdateDataException e) {
                    Toast.makeText(this, getResources().getString(R.string.task_actions_service_could_not_set_done), Toast.LENGTH_SHORT).show();
                }

                //Dismiss the notification
                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(taskId);
            }
        }
    }
}
