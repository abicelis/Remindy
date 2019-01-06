package ve.com.abicelis.remindy.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.Constants;
import ve.com.abicelis.remindy.app.activities.HomeActivity;
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

        //Create notification channel if running Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            createNotificationChannel(notificationManager, Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
        }

        //Intent for "DONE" button on BigView style
        Intent setTaskDoneIntent = new Intent(context, TaskActionsIntentService.class);
        setTaskDoneIntent.setAction(TaskActionsIntentService.ACTION_SET_TASK_DONE);
        setTaskDoneIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, task.getId());
        PendingIntent setTaskDonePendingIntent = PendingIntent.getService(context, task.getId(), setTaskDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for "POSTPONE" button on BigView style
        Intent postponeTaskIntent = new Intent(context, TaskActionsIntentService.class);
        postponeTaskIntent.setAction(TaskActionsIntentService.ACTION_POSTPONE_TASK);
        postponeTaskIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, task.getId());
        PendingIntent postponeTaskPendingIntent = PendingIntent.getService(context, task.getId(), postponeTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                .setSmallIcon(R.drawable.icon_remindy_notification_small)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setContentText(contentText)

                //Intent when clicking notification content
                .setContentIntent(openTaskPendingIntent)
                .setAutoCancel(true)

                //Make notification pop-up in post 5.0 devices
                .setPriority(Notification.PRIORITY_HIGH)

                //BigView
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(contentText))
                .addAction(R.drawable.icon_notification_done, context.getResources().getString(R.string.notification_big_view_set_done), setTaskDonePendingIntent)
                .addAction(R.drawable.icon_notification_postpone, context.getResources().getString(R.string.notification_big_view_postpone), postponeTaskPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(task.getId(), mBuilder.build());
    }


    public static void displayLocationBasedNotification(Context context, int notificationId, String contentTitle, String contentText,
                                                        List<Task> triggeredTasks) {

        //Create notification channel if running Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            createNotificationChannel(notificationManager, Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
        }


        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_remindy_notification_small)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)

                //Make notification pop-up in post 5.0 devices
                .setPriority(Notification.PRIORITY_HIGH);

        if(triggeredTasks.size() == 1) {

            //Intent for "DONE" button on BigView style
            Intent setTaskDoneIntent = new Intent(context, TaskActionsIntentService.class);
            setTaskDoneIntent.setAction(TaskActionsIntentService.ACTION_SET_TASK_DONE);
            setTaskDoneIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, triggeredTasks.get(0).getId());
            PendingIntent setTaskDonePendingIntent = PendingIntent.getService(context, triggeredTasks.get(0).getId(), setTaskDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Intent for clicking notification
            Intent openTaskIntent = new Intent(context, TaskDetailActivity.class);
            openTaskIntent.putExtra(TASK_ID_TO_DISPLAY, triggeredTasks.get(0).getId());
            //openTaskIntent.setAction(String.valueOf(task.getId()));
            //openTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //openTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack
            stackBuilder.addParentStack(TaskDetailActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(openTaskIntent);
            // Gets a PendingIntent containing the entire back stack
            PendingIntent openTaskPendingIntent = stackBuilder.getPendingIntent(triggeredTasks.get(0).getId(), PendingIntent.FLAG_UPDATE_CURRENT);


            //Add pendingIntent to notification
            mBuilder.setContentIntent(openTaskPendingIntent);
            mBuilder.addAction(R.drawable.icon_notification_done, context.getResources().getString(R.string.notification_big_view_set_done), setTaskDonePendingIntent);

            //Set BigView
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(contentText));

        } else {

            //Intent for clicking notification
            Intent openHomeIntent = new Intent(context, HomeActivity.class);
            PendingIntent openHomePendingItent = PendingIntent.getActivity(context, Integer.MAX_VALUE, openHomeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //InboxStyle BigView (Multiline!)
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(contentTitle);
            for (Task task: triggeredTasks)
                inboxStyle.addLine("- " + task.getTitle());

            //Add pendingIntent to notification
            mBuilder.setContentIntent(openHomePendingItent);

            //Set BigView
            mBuilder.setStyle(inboxStyle);
        }


        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(triggeredTasks.get(0).getId(), mBuilder.build());
    }





    //Needed for Android 8 Oreo +
    private static void createNotificationChannel(NotificationManager notificationManager, @NonNull String channelId, String channelName, String channelDescription) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}
