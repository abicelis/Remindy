package ve.com.abicelis.remindy.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.PlaceActivity;
import ve.com.abicelis.remindy.app.interfaces.TaskDataInterface;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.Reminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.SnackbarUtil;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by abice on 25/4/2017.
 */

public class ReminderFragment extends Fragment implements TaskDataInterface {

    //CONST
    private static final String TAG = ReminderFragment.class.getSimpleName();
    public static final String TASK_ARGUMENT = "TASK_ARGUMENT";

    //Used when there are no Places in DB and user is trying to add a Location-based reminder to a new task, so user is sent to PlaceActivity
    public static final int REQUEST_CODE_CREATING_PLACE_FOR_NEW_TASK = 128;

    //DATA
    private boolean useReminderFlag;
    private Task mTask = new Task();
    private Reminder mReminder;
    private ReminderType mReminderType = ReminderType.NONE;

    //UI
    private LinearLayout mContainer;
    private Spinner mReminderTypeSpinner;
    private Fragment mFragment;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder, container, false);

        //Grab task argument
        if(getArguments().containsKey(TASK_ARGUMENT)) {
            mTask = (Task) getArguments().get(TASK_ARGUMENT);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().setResult(RESULT_CANCELED);
                    getActivity().finish();
                }
            };
            Log.e(TAG, "Missing TASK_ARGUMENT argument in ReminderFragment.");
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_container);
        mReminderTypeSpinner = (Spinner) rootView.findViewById(R.id.fragment_reminder_reminder_type);

        setupSpinners();
        setTaskValues();
        return rootView;
    }



    private void setupSpinners() {
        List<String> reminderTypes = ReminderType.getFriendlyValues(getActivity());
        ArrayAdapter reminderTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, reminderTypes);
        reminderTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mReminderTypeSpinner.setAdapter(reminderTypeAdapter);
        mReminderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleReminderTypeSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setTaskValues() {
        if (mTask.getReminderType() != ReminderType.NONE) {
            useReminderFlag = true;
            mReminderTypeSpinner.setSelection(mTask.getReminderType().ordinal());
        }
    }


    private void handleReminderTypeSelection(int position) {
        Bundle bundle = new Bundle();
        mReminderType = ReminderType.values()[position];
        switch (mReminderType) {
            case ONE_TIME:
                mReminder = (useReminderFlag ? mTask.getReminder() : new OneTimeReminder());
                mFragment = new EditOneTimeReminderFragment();
                bundle.putSerializable(EditRepeatingReminderFragment.REMINDER_ARGUMENT, mReminder);
                mFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_reminder_reminder_placeholder, mFragment).commit();
                break;

            case REPEATING:
                mReminder = (useReminderFlag ? mTask.getReminder() : new RepeatingReminder());
                mFragment = new EditRepeatingReminderFragment();
                bundle.putSerializable(EditRepeatingReminderFragment.REMINDER_ARGUMENT, mReminder);
                mFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_reminder_reminder_placeholder, mFragment).commit();
                break;

            case LOCATION_BASED:
                if(atLeastOnePlaceExists()) {
                    handleLocationBasedTaskReminderSelected();
                } else
                    handleNoPlacesExist();
                break;

            case NONE:
                if(mFragment != null)
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                mFragment = null;
                mReminder = null;
                break;
        }

        //Reset flag if used
        useReminderFlag = false;
    }


    private boolean atLeastOnePlaceExists() {
        return (new RemindyDAO(getActivity()).getPlaces().size() > 0);
    }

    private void handleLocationBasedTaskReminderSelected() {
        Bundle bundle = new Bundle();
        mReminder = (useReminderFlag ? mTask.getReminder() : new LocationBasedReminder());
        mFragment = new EditLocationBasedReminderFragment();
        bundle.putSerializable(EditLocationBasedReminderFragment.REMINDER_ARGUMENT, mReminder);
        mFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_reminder_reminder_placeholder, mFragment).commit();
    }

    public void handleNoPlacesExist() {
        //No Places exist, alert user and allow him/her to create a place.

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.fragment_reminder_no_places_dialog_title))
                .setMessage(getResources().getString(R.string.fragment_reminder_no_places_dialog_message))
                .setPositiveButton(getResources().getString(R.string.fragment_reminder_no_places_dialog_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent createAPlaceIntent = new Intent(getActivity(), PlaceActivity.class);
                        startActivityForResult(createAPlaceIntent, REQUEST_CODE_CREATING_PLACE_FOR_NEW_TASK);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.fragment_reminder_no_places_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mReminderTypeSpinner.setSelection(0);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CREATING_PLACE_FOR_NEW_TASK) {
            if(atLeastOnePlaceExists()) {
                handleLocationBasedTaskReminderSelected();
            } else
                //User failed to add a Place, set reminder to none.
                mReminderTypeSpinner.setSelection(0);
        }

    }

    @Override
    public void updateData() {
        if(mFragment != null)
            ((TaskDataInterface) mFragment).updateData();

        if(mReminder != null) {
            mTask.setReminder(mReminder);
            mTask.setReminderType(mReminder.getType());
            mTask.setStatus(TaskStatus.PROGRAMMED);

        } else {
            mTask.setReminderType(ReminderType.NONE);
            mTask.setReminder(null);
            mTask.setStatus(TaskStatus.UNPROGRAMMED);

        }
    }
}
