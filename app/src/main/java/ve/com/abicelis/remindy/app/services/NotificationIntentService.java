package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.util.AlarmManagerUtil;
import ve.com.abicelis.remindy.util.CalendarUtil;
import ve.com.abicelis.remindy.util.GeofenceUtil;
import ve.com.abicelis.remindy.util.NotificationUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.viewmodel.TaskTriggerViewModel;

/**
 * Created by abice on 29/4/2017.
 */

public class NotificationIntentService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //CONST
    private static final String TAG = NotificationIntentService.class.getSimpleName();

    //DATA
    private GoogleApiClient mGoogleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        //Reset triggered tasks
        SharedPreferenceUtil.setTriggeredTaskList(new ArrayList<Integer>(), getApplicationContext());

        //Update Alarms
        AlarmManagerUtil.updateAlarms(getApplicationContext());


        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    /* GoogleApiClient callbacks */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected()");
        GeofenceUtil.addGeofences(getApplicationContext(), mGoogleApiClient);
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended()");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");

    }

}
