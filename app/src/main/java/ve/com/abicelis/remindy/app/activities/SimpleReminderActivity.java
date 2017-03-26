package ve.com.abicelis.remindy.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.transitionseverywhere.TransitionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.TaskCategory;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.model.attachment.Attachment;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.util.InputFilterMinMax;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 16/3/2017.
 */

public class SimpleReminderActivity extends AppCompatActivity {

    //CONST
    public static final int INTENT_EDIT_EXTRAS_REQUEST_CODE = 293;
    public static final String ARG_SIMPLE_REMINDER = "ARG_SIMPLE_REMINDER";
    public static final String KEY_INSTANCE_STATE_SIMPLE_REMINDER = "KEY_INSTANCE_STATE_SIMPLE_REMINDER";

    //DATA
    private List<String> reminderCategories;
    private List<String> reminderRepeatTypes;
    private List<String> reminderRepeatEndTypes;
    private boolean reminderRepeatActive = false;
    Calendar mDateCal;
    Time mTimeTime;
    Calendar mRepeatUntilCal;
    RepeatingReminder mReminder = null;


    //UI
    private Toolbar mToolbar;
    private EditText mTitle;
    private EditText mDescription;
    private Spinner mCategory;
    private EditText mDate;
    private EditText mTime;
    private Spinner mRepeatType;
    private LinearLayout mTransitionsContainer;
    private LinearLayout mRepeatContainer;
    private EditText mRepeatInterval;
    private TextView mRepeatTypeTitle;
    private LinearLayout mRepeatEndForEventsContainer;
    private LinearLayout mRepeatEndUntilContainer;
    private EditText mRepeatEndForXEvents;
    private Spinner mRepeatEndType;
    private EditText mRepeatUntilDate;
    private CalendarDatePickerDialogFragment mDatePicker;
    private CalendarDatePickerDialogFragment mRepeatUntilDatePicker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_reminder);


        mToolbar = (Toolbar) findViewById(R.id.activity_reminder_simple_toolbar);
        mTitle = (EditText) findViewById(R.id.activity_reminder_simple_title);
        mDescription = (EditText) findViewById(R.id.activity_reminder_simple_description);
        mCategory = (Spinner) findViewById(R.id.activity_reminder_simple_category);
        mDate = (EditText) findViewById(R.id.activity_reminder_simple_date);
        mTime = (EditText) findViewById(R.id.activity_reminder_simple_time);
        mRepeatType = (Spinner) findViewById(R.id.activity_reminder_simple_repeat_type);
        mTransitionsContainer = (LinearLayout) findViewById(R.id.activity_reminder_simple_transitions_container);
        mRepeatContainer = (LinearLayout) findViewById(R.id.activity_reminder_simple_repeat_container);
        mRepeatInterval = (EditText) findViewById(R.id.activity_reminder_simple_repeat_interval);
        mRepeatTypeTitle = (TextView) findViewById(R.id.activity_reminder_simple_repeat_type_title);
        mRepeatEndForEventsContainer = (LinearLayout) findViewById(R.id.activity_reminder_simple_repeat_end_for_events_container);
        mRepeatEndUntilContainer = (LinearLayout) findViewById(R.id.activity_reminder_simple_repeat_end_until_container);
        mRepeatEndForXEvents = (EditText) findViewById(R.id.activity_reminder_simple_repeat_for_x_events);
        mRepeatEndType = (Spinner) findViewById(R.id.activity_reminder_simple_repeat_end_type);
        mRepeatUntilDate = (EditText) findViewById(R.id.activity_reminder_simple_repeat_until);

        mRepeatInterval.setFilters(new InputFilter[]{new InputFilterMinMax("1", "99")});
        mRepeatEndForXEvents.setFilters(new InputFilter[]{new InputFilterMinMax("1", "99")});

        mToolbar.setTitle(R.string.activity_reminder_toolbar_title);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        setSupportActionBar(mToolbar);


        setupSpinners();
        setupDateAndTimePickers();

        //If editing a reminder, then intent contains a RepeatingReminder extra with key ARG_SIMPLE_REMINDER
        //Get the reminder and restore its data
        if(getIntent().hasExtra(ARG_SIMPLE_REMINDER)) {
            mReminder = (RepeatingReminder) getIntent().getSerializableExtra(ARG_SIMPLE_REMINDER);
            restoreSimpleReminder();
        }

        //If a state was saved (such as when rotating the device), restore the state!
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_INSTANCE_STATE_SIMPLE_REMINDER)) {
            mReminder = (RepeatingReminder) savedInstanceState.getSerializable(KEY_INSTANCE_STATE_SIMPLE_REMINDER);
            restoreSimpleReminder();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the state before killing the activity (maybe the device rotated?)
        saveSimpleReminder();

        outState.putSerializable(KEY_INSTANCE_STATE_SIMPLE_REMINDER, mReminder);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_reminder_simple_exit_dialog_title))
                .setMessage(getResources().getString(R.string.activity_reminder_simple_exit_dialog_message))
                .setPositiveButton(getResources().getString(R.string.activity_reminder_simple_exit_dialog_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_reminder_simple_exit_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void setupSpinners() {
        reminderCategories = TaskCategory.getFriendlyValues(this);
        ArrayAdapter reminderCategoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reminderCategories);
        reminderCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mCategory.setAdapter(reminderCategoryAdapter);

        reminderRepeatTypes = ReminderRepeatType.getFriendlyValues(this);
        ArrayAdapter reminderRepeatTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reminderRepeatTypes);
        reminderRepeatTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mRepeatType.setAdapter(reminderRepeatTypeAdapter);
        mRepeatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleRepeatTypeSelected(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        reminderRepeatEndTypes = ReminderRepeatEndType.getFriendlyValues(this);
        ArrayAdapter reminderRepeatEndTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, reminderRepeatEndTypes);
        reminderRepeatEndTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mRepeatEndType.setAdapter(reminderRepeatEndTypeAdapter);
        mRepeatEndType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleRepeatEndTypeSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDateAndTimePickers() {

        final Calendar mToday = Calendar.getInstance();
        final Calendar mTomorrow = Calendar.getInstance();
        mTomorrow.add(Calendar.DAY_OF_MONTH, 1);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(mDateCal == null) {
                                    mDateCal = Calendar.getInstance();
                                    mDateCal.set(Calendar.HOUR_OF_DAY, 0);
                                    mDateCal.set(Calendar.MINUTE, 0);
                                    mDateCal.set(Calendar.SECOND, 0);
                                    mDateCal.set(Calendar.MILLISECOND, 0);
                                }
                                mDateCal.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                                mDate.setText(formatter.format(mDateCal.getTime()));
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), mToday.get(Calendar.DAY_OF_MONTH))
                        .setDateRange(new MonthAdapter.CalendarDay(mToday), null)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                mDatePicker.show(getSupportFragmentManager(), "mDate");
            }
        });

        mRepeatUntilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepeatUntilDatePicker = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(mRepeatUntilCal == null) {
                                    mRepeatUntilCal = Calendar.getInstance();
                                    mRepeatUntilCal.set(Calendar.HOUR_OF_DAY, 0);
                                    mRepeatUntilCal.set(Calendar.MINUTE, 0);
                                    mRepeatUntilCal.set(Calendar.SECOND, 0);
                                    mRepeatUntilCal.set(Calendar.MILLISECOND, 0);
                                }
                                mRepeatUntilCal.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                                mRepeatUntilDate.setText(formatter.format(mRepeatUntilCal.getTime()));
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(mTomorrow.get(Calendar.YEAR), mTomorrow.get(Calendar.MONTH), mTomorrow.get(Calendar.DAY_OF_MONTH))
                        .setDateRange(new MonthAdapter.CalendarDay(mTomorrow), null)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                mRepeatUntilDatePicker.show(getSupportFragmentManager(), "mRepeatUntilDate");
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                if(mTimeTime == null) {
                                    mTimeTime = new Time();
                                    //TODO: grab timeFormat from preferences and mTimeTime.setDisplayTimeFormat();

                                }
                                mTimeTime.setHour(hourOfDay);
                                mTimeTime.setMinute(minute);
                                mTime.setText(mTimeTime.toString());
                            }
                        })
                        .setStartTime(12, 0)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                rtpd.show(getSupportFragmentManager(), "mTime");
            }
        });
    }



    private void handleRepeatTypeSelected(int position) {

//                TransitionSet set = new TransitionSet()
//                        .addTransition(new Scale(0.9f))
//                        .addTransition(new Fade())
//                        .setInterpolator(reminderRepeatActive ? new FastOutLinearInInterpolator() :
//                                new FastOutLinearInInterpolator());

//        if(ReminderRepeatType.values()[position] != ReminderRepeatType.DISABLED && !reminderRepeatActive) {
//            TransitionManager.beginDelayedTransition(mTransitionsContainer);
//            mRepeatContainer.setVisibility(View.VISIBLE);
//            reminderRepeatActive = true;
//
//        } else if(ReminderRepeatType.values()[position] == ReminderRepeatType.DISABLED && reminderRepeatActive) {
//            TransitionManager.beginDelayedTransition(mTransitionsContainer);
//            mRepeatContainer.setVisibility(View.INVISIBLE);
//            reminderRepeatActive = false;
//        }
//
//        switch(ReminderRepeatType.values()[position]) {
//            case DAILY:
//                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_days);
//                break;
//            case WEEKLY:
//                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_weeks);
//                break;
//            case MONTHLY:
//                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_months);
//                break;
//            case YEARLY:
//                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_years);
//                break;
//        }
//
//        if(ReminderRepeatType.values()[position] != ReminderRepeatType.DISABLED) {
//            mRepeatInterval.requestFocus();
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        }

    }

    private void handleRepeatEndTypeSelected(int position) {
        switch (ReminderRepeatEndType.values()[position]) {
            case FOREVER:
                TransitionManager.beginDelayedTransition(mTransitionsContainer);
                mRepeatEndForEventsContainer.setVisibility(View.GONE);
                mRepeatEndUntilContainer.setVisibility(View.GONE);
                break;

            case UNTIL_DATE:
                TransitionManager.beginDelayedTransition(mTransitionsContainer);
                mRepeatEndForEventsContainer.setVisibility(View.GONE);
                mRepeatEndUntilContainer.setVisibility(View.VISIBLE);
                break;

            case FOR_X_EVENTS:
                TransitionManager.beginDelayedTransition(mTransitionsContainer);
                mRepeatEndForEventsContainer.setVisibility(View.VISIBLE);
                mRepeatEndUntilContainer.setVisibility(View.GONE);
                mRepeatEndForXEvents.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_extras:
                saveSimpleReminder();
                Intent editExtrasIntent = new Intent(this, ReminderExtrasActivity.class);
//                if(mReminder.getAttachments().size() > 0)
//                    editExtrasIntent.putExtra(ReminderExtrasActivity.ARG_EXTRAS, mReminder.getAttachments());
//                startActivityForResult(editExtrasIntent, INTENT_EDIT_EXTRAS_REQUEST_CODE);
                break;
            case R.id.action_save:
                if(valuesAreGood()) {
                    saveSimpleReminder();
                    RemindyDAO dao = new RemindyDAO(this);
//                    try {
////                        dao.insertSimpleReminder(mReminder);
//
//                        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                            @Override
//                            public void onDismissed(Snackbar transientBottomBar, int event) {
//                                super.onDismissed(transientBottomBar, event);
//                                finish();
//                            }
//                        };
//                        SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.reminder_saved_successfully, SnackbarUtil.SnackbarDuration.SHORT, callback);
//
//                    } catch (CouldNotInsertDataException e) {
//                        SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_inserting_reminder, null, null);
//                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_EDIT_EXTRAS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data.hasExtra(ReminderExtrasActivity.ARG_EXTRAS)){
                ArrayList<Attachment> extras = ( ArrayList<Attachment>) data.getExtras().getSerializable(ReminderExtrasActivity.ARG_EXTRAS);
                //mReminder.setAttachments(extras);
                restoreSimpleReminder();
                //TODO: Maybe change add extras menu icon with amount of extras added?
            }
        }
    }

    private boolean valuesAreGood() {
        String title = mTitle.getText().toString();
        if(title.trim().isEmpty()) {
            SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_title, null, null);
            return false;
        }

        if(mDateCal == null) {
            SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_date, null, null);
            return false;
        }
        if(mTimeTime == null) {
            SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_time, null, null);
            mTime.requestFocus();
            return false;
        }

//        if(ReminderRepeatType.values()[mRepeatType.getSelectedItemPosition()] != ReminderRepeatType.DISABLED) {
//            if(mRepeatInterval.getText().toString().isEmpty()){
//                SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_repeat_interval, null, null);
//                return false;
//            }
//
//            if(ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()] == ReminderRepeatEndType.FOR_X_EVENTS) {
//                if(mRepeatEndForXEvents.getText().toString().isEmpty()){
//                    SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_repeat_events, null, null);
//                    return false;
//                }
//            }
//
//            if(ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()] == ReminderRepeatEndType.UNTIL_DATE) {
//                if(mRepeatUntilCal == null) {
//                    SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_invalid_repeat_until_date, null, null);
//                    return false;
//                }
//                if(mRepeatUntilCal.compareTo(mDateCal) <= 0) {
//                    SnackbarUtil.showSnackbar(mRepeatContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_repeat_until_date_after_reminder_date, null, null);
//                    return false;
//                }
//            }
//
//        }

        return true;
    }

    private void saveSimpleReminder() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        TaskCategory category = TaskCategory.values()[mCategory.getSelectedItemPosition()];
        ReminderRepeatType repeatType = ReminderRepeatType.values()[mRepeatType.getSelectedItemPosition()];

        int repeatInterval = 0;
        ReminderRepeatEndType repeatEndType = null;
        int repeatEndNumberOfEvents = 0;

//        if(repeatType != ReminderRepeatType.DISABLED) {
//            repeatInterval = Integer.parseInt(mRepeatInterval.getText().toString());
//            repeatEndType = ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()];
//
//            if(repeatEndType == ReminderRepeatEndType.FOR_X_EVENTS)
//                repeatEndNumberOfEvents = Integer.parseInt(mRepeatEndForXEvents.getText().toString());
//
//            if(repeatEndType != ReminderRepeatEndType.UNTIL_DATE)
//                mRepeatUntilCal = null;
//        }
//
//
//        ArrayList<Attachment> extras = new ArrayList<>();
//        if(mReminder != null)
//            extras = mReminder.getAttachments();
//
//        mReminder = new RepeatingReminder(TaskStatus.ACTIVE, title, description, category, mDateCal, mTimeTime, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, mRepeatUntilCal);
//        mReminder.setAttachments(extras);
    }

    private void restoreSimpleReminder() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

//
//        mTitle.setText(mReminder.getTitle());
//        mDescription.setText(mReminder.getDescription());
//
//        if(mReminder.getDate() != null) {
//            mDateCal = Calendar.getInstance();
//            mDateCal.setTimeInMillis(mReminder.getDate().getTimeInMillis());
//            mDate.setText(formatter.format(mDateCal.getTime()));
//        }
//
//        if(mReminder.getTime() != null) {
//            mTime.setText(mReminder.getTime().toString());
//            mTimeTime = new Time(mReminder.getTime().getTimeInMinutes());
//        }
//
//        mCategory.setSelection(mReminder.getCategory().ordinal());
//        mRepeatType.setSelection(mReminder.getRepeatType().ordinal());
//
//
//        if(mReminder.getRepeatType() != ReminderRepeatType.DISABLED) {
//            mRepeatInterval.setText(String.valueOf(mReminder.getRepeatInterval()));
//            mRepeatEndType.setSelection(mReminder.getRepeatEndType().ordinal());
//            handleRepeatTypeSelected(mReminder.getRepeatType().ordinal());
//
//            if(mReminder.getRepeatEndType() == ReminderRepeatEndType.FOR_X_EVENTS)
//                mRepeatEndForXEvents.setText(String.valueOf(mReminder.getRepeatEndNumberOfEvents()));
//
//            if(mReminder.getRepeatEndType() == ReminderRepeatEndType.UNTIL_DATE) {
//                if(mReminder.getRepeatEndDate() != null) {
//                    mRepeatUntilCal = Calendar.getInstance();
//                    mRepeatUntilCal.setTimeInMillis(mReminder.getRepeatEndDate().getTimeInMillis());
//                    mRepeatUntilDate.setText(formatter.format(mRepeatUntilCal.getTime()));
//                }
//            }
//
//            if(mReminder.getRepeatEndType() != ReminderRepeatEndType.FOREVER) {
//                handleRepeatEndTypeSelected(mReminder.getRepeatEndType().ordinal());
//
//            }
//
//        }
    }

}
