package ve.com.abicelis.remindy.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.TaskDetailActivity;
import ve.com.abicelis.remindy.app.services.TaskActionsIntentService;
import ve.com.abicelis.remindy.model.Task;

import static android.content.Context.NOTIFICATION_SERVICE;
import static ve.com.abicelis.remindy.app.activities.TaskDetailActivity.TASK_ID_TO_DISPLAY;

/**
 * Created by abice on 1/5/2017.
 */

public class NotificationUtil {

    public static void displayNotification(Context context, Task task, String contentTitle, String contentText) {

        //Intent for "DONE" button on BigView style
        Intent setTaskDoneIntent = new Intent(context, TaskActionsIntentService.class);
        setTaskDoneIntent.setAction(TaskActionsIntentService.ACTION_SET_TASK_DONE);
        setTaskDoneIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, task.getId());
        PendingIntent setTaskDonePendingIntent = PendingIntent.getService(context, task.getId(), setTaskDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for clicking notification
        Intent openTaskIntent = new Intent(context, TaskDetailActivity.class);
        openTaskIntent.putExtra(TASK_ID_TO_DISPLAY, task.getId());
        //openTaskIntent.setAction(String.valueOf(task.getId()));
        //openTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //openTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack
        stackBuilder.addParentStack(TaskDetailActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(openTaskIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent openTaskPendingIntent = stackBuilder.getPendingIntent(task.getId(), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(openTaskPendingIntent)
                .setSmallIcon(R.drawable.icon_remindy_notification)
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setAutoCancel(true)
                .setContentText(contentText)

                //Make notification pop-up in post 5.0 devices
                .setPriority(Notification.PRIORITY_HIGH)

                //BigView
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(contentText))
                .addAction (R.drawable.icon_check_fab_mini, context.getResources().getString(R.string.notification_big_view_set_done), setTaskDonePendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(task.getId(), mBuilder.build());
    }

    public static void displayLocationBasedNotification(Context context, int notificationId, String contentTitle, String contentText) {

        //TODO: click intent here... ?


        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_remindy_notification)
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentText(contentText);

        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId, mBuilder.build());
    }
}
