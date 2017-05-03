package ve.com.abicelis.remindy.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.util.CalendarUtil;
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
    private static final int SLEEP_MILLIS = 10 * 60 * 1000;
    private static final int SLEEP_MILLIS_IDLE = 1 * 60 * 1000;
    private static final int NOTIFICATION_ID_NORMAL = 999;


    //DATA
    private RemindyDAO mDao;
    private GoogleApiClient mGoogleApiClient;
    private TaskTriggerViewModel mNextTaskToTrigger;
    private long mSleepTime;
    private Integer mLastTaskTriggered = null;


    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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

            //Refresh task to trigger, avoiding last triggered task if exists
            loadNextTaskToTrigger(mLastTaskTriggered);

            if(mNextTaskToTrigger != null) {
                Calendar now = Calendar.getInstance();
                int triggerMinutesBeforeNotification = SharedPreferenceUtil.getTriggerMinutesBeforeNotification(getApplicationContext());

                long differenceInMinutes = CalendarUtil.getDifferenceMinutesBetween(mNextTaskToTrigger.getTriggerDateWithTime(), now);

                if(differenceInMinutes < triggerMinutesBeforeNotification ) {
                    handleNotification(triggerMinutesBeforeNotification);
                    mLastTaskTriggered = mNextTaskToTrigger.getTask().getId();
                } else {
                    mSleepTime = SLEEP_MILLIS_IDLE;
                }

            } else {
                //Idle sleep
                mSleepTime = SLEEP_MILLIS_IDLE;
            }



            try {
                Thread.sleep(mSleepTime);
            } catch (InterruptedException e) { /* Do nothing */ }







//            try {
//                mNextTaskToTrigger = mDao.getNextTaskToTrigger(null);
//            } catch (CouldNotGetDataException e ) {
//                mNextTaskToTrigger = null;
//            }
//
//            if(mNextTaskToTrigger != null) {
//                String contentTitle = "Task '" + mNextTaskToTrigger.getTask().getTitle() + "'";
//                String contentText = "Will trigger " +
//                        SharedPreferenceUtil.getDateFormat(getApplicationContext()).formatCalendar(mNextTaskToTrigger.getTriggerDate()) +
//                        " at " +
//                        mNextTaskToTrigger.getTriggerTime().toString();
//
//                NotificationUtil.displayNotification(this, NOTIFICATION_ID_NORMAL, contentTitle, contentText);
//            }
//
//            try {
//                Thread.sleep(SLEEP_MILLIS);
//            } catch (InterruptedException e) { /* Do nothing */ }
        }
    }



    private void loadNextTaskToTrigger(Integer exceptForThisTaskId ) {
        try {
            mNextTaskToTrigger = mDao.getNextTaskToTrigger(exceptForThisTaskId);
        } catch (CouldNotGetDataException e ) {
            mNextTaskToTrigger = null;
        }
    }

    private void handleNotification(int triggerMinutesBeforeNotification) {
        String contentTitle = String.format(Locale.getDefault(), getResources().getString(R.string.notification_service_normal_title), mNextTaskToTrigger.getTask().getTitle());
        String contentText = String.format(Locale.getDefault(), getResources().getString(R.string.notification_service_normal_text), triggerMinutesBeforeNotification);
        NotificationUtil.displayNotification(this, NOTIFICATION_ID_NORMAL, contentTitle, contentText);
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
