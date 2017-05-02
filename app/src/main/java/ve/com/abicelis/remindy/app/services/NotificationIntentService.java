package ve.com.abicelis.remindy.app.services;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.util.NotificationUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.viewmodel.TaskTriggerViewModel;

/**
 * Created by abice on 29/4/2017.
 */

public class NotificationIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //CONSTS
    private static final int SLEEP_MILLIS = 60000;
    private static final int REQ_CODE_GPS_FINE_PERMISSION = 2;                 // Permission request codes need to be < 256
    private static final long NEVER_EXPIRE = -1;
    private static final int ID_NORMAL = 001;
    private static final int ID_GPS = 002;


    //DATA
    private boolean flag;
    private RemindyDAO mDao;
    private TaskTriggerViewModel nextTaskToTrigger;
    private GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;


    public NotificationIntentService() {
        super("NotificationIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mDao = new RemindyDAO(getApplicationContext());

        buildGeofenceList();
        mGeofencePendingIntent = getGeofencePendingIntent();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }


        while (true) {

            if(flag) {
                try {
                    nextTaskToTrigger = mDao.getNextTaskToTrigger();
                } catch (CouldNotGetDataException e ) {
                    nextTaskToTrigger = null;
                }

                if(nextTaskToTrigger != null) {
                    String contentTitle = "Task '" + nextTaskToTrigger.getTask().getTitle() + "'";
                    String contentText = "Will trigger " +
                            SharedPreferenceUtil.getDateFormat(getApplicationContext()).formatCalendar(nextTaskToTrigger.getTriggerDate()) +
                            " at " +
                            nextTaskToTrigger.getTriggerTime().toString();

                    NotificationUtil.displayNotification(this, ID_NORMAL, contentTitle, contentText);


                }
            }

            try {
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) {
                        /* Do nothing */
            }

        }
    }

    private void buildGeofenceList() {
        for (Place place :mDao.getPlaces()){
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(String.valueOf(place.getId()))
                    .setCircularRegion(
                            place.getLatitude(),
                            place.getLongitude(),
                            500
                    )
                    .setExpirationDuration(NEVER_EXPIRE)
                    .setLoiteringDelay(30 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build());

        }
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, LocationReminderGeofenceIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent =  PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //flag = true;
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()) {
                        //Awesome... yay....
                    }
                }
            });
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
