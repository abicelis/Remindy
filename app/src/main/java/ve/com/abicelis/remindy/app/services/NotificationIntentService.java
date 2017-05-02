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
import ve.com.abicelis.remindy.util.GeofenceUtil;
import ve.com.abicelis.remindy.util.NotificationUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.viewmodel.TaskTriggerViewModel;

/**
 * Created by abice on 29/4/2017.
 */

public class NotificationIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //CONSTS
    private static final int TRIGGER_NOTIFICATION_BEFORE_TIME = 5;      //Minutes before a reminder to trigger a notification
    private static final int SLEEP_MILLIS = 10 * 60 * 1000;
    private static final long NEVER_EXPIRE = -1;
    private static final int NOTIFICATION_ID_NORMAL = 999;


    //DATA
    private RemindyDAO mDao;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;



    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //TODO: build something so that when the user modifies a place or a task with a location-reminder, the geofences get updated
        //HMMM refactor esto a un util? o algo un Controller? y llamar ese update que me refiero arriba desde el propio app!!!!


        mDao = new RemindyDAO(getApplicationContext());


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
            TaskTriggerViewModel nextTaskToTrigger;
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

                NotificationUtil.displayNotification(this, NOTIFICATION_ID_NORMAL, contentTitle, contentText);
            }

            try {
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) {
                    /* Do nothing */
            }
        }
    }

    @Override
    public void onDestroy() {
        GeofenceUtil.removeGeofences(getApplicationContext(), mGoogleApiClient);
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }




    /* GoogleApiClient callbacks */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GeofenceUtil.addGeofences(getApplicationContext(), mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}





}
