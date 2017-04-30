package ve.com.abicelis.remindy.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by abice on 29/4/2017.
 */

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startNotificationServiceIntent = new Intent(context, NotificationIntentService.class);
        context.startService(startNotificationServiceIntent);
    }
}
