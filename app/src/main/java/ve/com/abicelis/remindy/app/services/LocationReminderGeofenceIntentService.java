package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.NotificationUtil;


/**
 * Created by abice on 30/4/2017.
 */

public class LocationReminderGeofenceIntentService extends IntentService {

    //CONST
    private static final String TAG = LocationReminderGeofenceIntentService.class.getSimpleName();
    //private static final int NOTIFICATION_ID_GPS = 002;


    public LocationReminderGeofenceIntentService() {
        super("LocationReminderGeofenceIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }


        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
                ) {

            for(Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
                List<Task> tasks = new ArrayList<>();
                try {
                    tasks = new RemindyDAO(this).getLocationBasedTasksAssociatedWithPlace(Integer.valueOf(geofence.getRequestId()), geofenceTransition);
                }catch (CouldNotGetDataException e) {/* Do nothing */}

                if(tasks.size() > 0) {
                    String notificationTitle = getGeofenceNotificationTitle(geofenceTransition, geofence);
                    String notificationText = getGeofenceNotificationText(tasks);

                    // Send notification and log the transition details.
                    NotificationUtil.displayNotification(this, Integer.valueOf(geofence.getRequestId()), notificationTitle, notificationText);
                    Log.i(TAG, notificationTitle + notificationText);
                }

            }

        } else {
            // Log the error.
            Log.e(TAG, getResources().getString(R.string.geofence_transition_invalid_type) +"= " + geofenceTransition);
        }

    }

    private String getGeofenceNotificationTitle(int geofenceTransition, Geofence triggeringGeofence) {
        String transition = "";
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transition = getResources().getString(R.string.geofence_enter);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transition = getResources().getString(R.string.geofence_exit);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                transition = getResources().getString(R.string.geofence_dwell);
                break;
        }

        List<Place> places = new RemindyDAO(this).getPlaces();

        return String.format(Locale.getDefault(),
                getResources().getString(R.string.geofence_notification_title),
                transition,
                places.get(Integer.valueOf(triggeringGeofence.getRequestId())).getAlias());

    }
    private String getGeofenceNotificationText(List<Task> tasks) {
        String tasksStr = "";

        for (Task task: tasks)
            tasksStr += task.getTitle() + "\n\r";


        return String.format(Locale.getDefault(),
                getResources().getString(R.string.geofence_notification_text),
                tasksStr);
    }

}


