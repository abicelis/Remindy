package ve.com.abicelis.remindy.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.fragments.EditLocationBasedReminderFragment;
import ve.com.abicelis.remindy.app.fragments.EditOneTimeReminderFragment;
import ve.com.abicelis.remindy.app.fragments.EditRepeatingReminderFragment;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.Reminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 20/4/2017.
 */

public class AddReminderActivity extends AppCompatActivity {

    //CONSTS
    public static final int ADD_REMINDER_REQUEST_CODE = 51;
    public static final String ADD_REMINDER_RETURN_REMINDER = "ADD_REMINDER_RETURN_REMINDER";
    public static final int RESULT_KEEP = 10;   //Used by NewTaskActivity, save reminder and stay in that activity
    public static final int RESULT_SAVE = 11;   //Used by NewTaskActivity, save reminder and go home


    //DATA
    private Reminder mReminder;
    private ReminderType mReminderType = ReminderType.NONE;

    //UI
    private LinearLayout mContainer;
    private Toolbar mToolbar;
    private Spinner mReminderTypeSpinner;
    private Fragment mFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);



        mContainer = (LinearLayout) findViewById(R.id.activity_add_reminder_container);
        mReminderTypeSpinner = (Spinner) findViewById(R.id.activity_add_reminder_reminder_type);

        setupSpinners();
        setUpToolbar();
    }

    private void setupSpinners() {
        List<String> reminderTypes = ReminderType.getFriendlyValues(this);
        ArrayAdapter reminderTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reminderTypes);
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

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_add_reminder_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_add_reminder_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }




    private void handleReminderTypeSelection(int position) {
        Bundle bundle = new Bundle();
        mReminderType = ReminderType.values()[position];
        switch (mReminderType) {
            case ONE_TIME:
                mReminder = new OneTimeReminder();
                mFragment = new EditOneTimeReminderFragment();
                bundle.putSerializable(EditRepeatingReminderFragment.REMINDER_ARGUMENT, mReminder);
                mFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_add_reminder_reminder_placeholder, mFragment).commit();
                break;

            case REPEATING:
                mReminder = new RepeatingReminder();
                mFragment = new EditRepeatingReminderFragment();
                bundle.putSerializable(EditRepeatingReminderFragment.REMINDER_ARGUMENT, mReminder);
                mFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_add_reminder_reminder_placeholder, mFragment).commit();
                break;

            case LOCATION_BASED:
                mReminder = new LocationBasedReminder();
                mFragment = new EditLocationBasedReminderFragment();
                bundle.putSerializable(EditLocationBasedReminderFragment.REMINDER_ARGUMENT, mReminder);
                mFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_add_reminder_reminder_placeholder, mFragment).commit();
                break;

            case NONE:
                if(mFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                mFragment = null;
                mReminder = null;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_add_reminder_back_dialog_title))
                .setMessage(getResources().getString(R.string.activity_add_reminder_back_dialog_message))
                .setPositiveButton(getResources().getString(R.string.activity_add_reminder_back_dialog_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateReminderValues();
                        if(checkReminderValues()) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(ADD_REMINDER_RETURN_REMINDER, mReminder);
                            setResult(RESULT_KEEP, returnIntent);
                            finish();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_add_reminder_back_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);       //Discard reminder
                        finish();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_add_reminder_save:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.activity_add_reminder_save_dialog_title))
                        .setMessage(getResources().getString(R.string.activity_add_reminder_save_dialog_message))
                        .setPositiveButton(getResources().getString(R.string.activity_add_reminder_save_dialog_positive),  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateReminderValues();
                                if(checkReminderValues()) {
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(ADD_REMINDER_RETURN_REMINDER, mReminder);
                                    setResult(RESULT_SAVE, returnIntent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.activity_add_reminder_save_dialog_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();       //Cancelled, dismiss
                            }
                        })
                        .create();
                dialog.show();
                break;
        }

        return false;
    }


    private void updateReminderValues() {
        if(mFragment != null)
            ((AddReminderActivity.ReminderValueUpdater) mFragment).updateReminderValues();
    }

    private boolean checkReminderValues() {
        //TODO: check reminder values!

        //Hide keyboard
        ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContainer.getWindowToken(), 0);

        switch (mReminderType) {
            case NONE:
                return true;

            case ONE_TIME:
                if( ((OneTimeReminder)mReminder).getDate() == null) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_one_time_snackbar_error_invalid_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((OneTimeReminder)mReminder).getTime() == null) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_one_time_snackbar_error_invalid_time, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                break;

            case REPEATING:
                if( ((RepeatingReminder)mReminder).getDate() == null) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_invalid_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((RepeatingReminder)mReminder).getTime() == null) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_invalid_time, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((RepeatingReminder)mReminder).getRepeatInterval() < 1) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_invalid_interval, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                switch ( ((RepeatingReminder)mReminder).getRepeatEndType()) {
                    case FOR_X_EVENTS:
                        if( ((RepeatingReminder)mReminder).getRepeatEndNumberOfEvents() < 1) {
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_invalid_number_events, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        break;
                    case UNTIL_DATE:
                        if( ((RepeatingReminder)mReminder).getRepeatEndDate() == null) {
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_invalid_end_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        if( ((RepeatingReminder)mReminder).getRepeatEndDate().compareTo(((RepeatingReminder)mReminder).getDate()) <= 0) {
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_add_reminder_repeating_snackbar_error_end_date_before_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        break;
                }
                break;

            case LOCATION_BASED:
                break;
        }

        return true;
    }

    public interface ReminderValueUpdater {
        void updateReminderValues();
    }
}
