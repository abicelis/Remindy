package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.util.NotificationUtil;


/**
 * Created by abice on 30/4/2017.
 */

public class LocationReminderGeofenceIntentService extends IntentService {

    //DATA
    List<Place> mPlaces;

    //CONST
    private static final String TAG = LocationReminderGeofenceIntentService.class.getSimpleName();

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
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            mPlaces = new RemindyDAO(this).getPlaces();

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            NotificationUtil.displayNotification(this, 2, "GPS", geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            //Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }

    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String str = "You ";

        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                str += getResources().getString(R.string.geofence_enter);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                str += getResources().getString(R.string.geofence_exit);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                str += getResources().getString(R.string.geofence_dwell);
                break;
        }

        for (Geofence geofence : triggeringGeofences) {
            str += mPlaces.get(Integer.valueOf(geofence.getRequestId())).getAlias();
        }

        return str;
    }

}


