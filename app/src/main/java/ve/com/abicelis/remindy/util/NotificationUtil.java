package ve.com.abicelis.remindy.util;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import ve.com.abicelis.remindy.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by abice on 1/5/2017.
 */

public class NotificationUtil {

    public static void displayNotification(Context context, int notificationId, String contentTitle, String contentText) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_remindy_notification)
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        // Sets an ID for the notification
        //int mNotificationId = 001;

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId, mBuilder.build());
    }
}
