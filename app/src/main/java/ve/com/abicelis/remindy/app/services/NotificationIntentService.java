package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;

/**
 * Created by abice on 29/4/2017.
 */

public class NotificationIntentService extends IntentService {

    //CONSTS
    private static final int SLEEP_MILLIS = 10000;

    //DATA
    private RemindyDAO mDao;

    //UI
    NotificationCompat.Builder mBuilder;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mDao = new RemindyDAO(getApplicationContext());

        while (true) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_remindy)
                    .setContentTitle("Remindy")
                    .setContentText("Hey you entered Home, or something!");

            // Sets an ID for the notification
            int mNotificationId = 001;

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            try {
                Thread.sleep(SLEEP_MILLIS);
            }catch (InterruptedException e) {
                /* Do nothing */
            }
        }
    }
}
