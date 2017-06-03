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
import ve.com.abicelis.remindy.exception.PlaceNotFoundException;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.NotificationUtil;


/**
 * Created by abice on 30/4/2017.
 */

public class GeofenceNotificationIntentService extends IntentService {

    //CONST
    private static final String TAG = GeofenceNotificationIntentService.class.getSimpleName();


    public GeofenceNotificationIntentService() {
        super("GeofenceNotificationIntentService");
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

        // Log the transition type
        handleLogTransition(geofenceTransition);


        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
                //|| geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER   //Skipping this transition because of alert spam issues
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
                    NotificationUtil.displayLocationBasedNotification(this, Integer.valueOf(geofence.getRequestId()), notificationTitle, notificationText, tasks);
                    Log.i(TAG, notificationTitle + " " + notificationText);
                }

            }

        }

    }

    private void handleLogTransition(int geofenceTransition) {

        String transitionStr = "";
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionStr = "GEOFENCE_TRANSITION_ENTER";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionStr = "GEOFENCE_TRANSITION_EXIT";
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                transitionStr = "GEOFENCE_TRANSITION_DWELL";
                break;
        }
        Log.d(TAG, transitionStr + " detected! ");
    }

    private String getGeofenceNotificationTitle(int geofenceTransition, Geofence triggeringGeofence) {
        String transition = "";
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transition = getResources().getString(R.string.notification_service_geofence_enter);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transition = getResources().getString(R.string.notification_service_geofence_exit);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                //transition = getResources().getString(R.string.notification_service_geofence_dwell);
                transition = getResources().getString(R.string.notification_service_geofence_enter);

                break;
        }
        int placeId = Integer.valueOf(triggeringGeofence.getRequestId());

        try {
            Place place = new RemindyDAO(this).getPlace(placeId);
            return String.format(Locale.getDefault(),
                    getResources().getString(R.string.notification_service_geofence_title),
                    transition,
                    place.getAlias());
        } catch (PlaceNotFoundException e) {
            return "";
        }


    }
    private String getGeofenceNotificationText(List<Task> tasks) {
        String tasksStr = "";

        for (Task task: tasks)
            tasksStr += task.getTitle() + ",";

        tasksStr = tasksStr.substring(0, tasksStr.length()-1);


        return String.format(Locale.getDefault(),
                getResources().getString(R.string.notification_service_geofence_text),
                tasksStr);
    }
}


