package ve.com.abicelis.remindy.app.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.model.SimpleReminder;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.util.InputFilterMinMax;

/**
 * Created by abice on 16/3/2017.
 */

public class SimpleReminderActivity extends AppCompatActivity {

    //CONST
    final Calendar mToday = Calendar.getInstance();
    final Calendar mTomorrow = Calendar.getInstance();
    public static final String ARG_SIMPLE_REMINDER = "ARG_SIMPLE_REMINDER";

    //DATA
    private List<String> reminderCategories;
    private List<String> reminderRepeatTypes;
    private List<String> reminderRepeatEndTypes;
    private boolean reminderRepeatActive = false;
    Calendar mDateCal;
    Time mTimeTime;
    Calendar mRepeatUntilCal;
    SimpleReminder mNewReminder = null;


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
        Log.d("SUPERTAG", "onCreate");


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

        //Try to get ARG_SIMPLE_REMINDER
        if(getIntent().hasExtra(ARG_SIMPLE_REMINDER)) {
            mNewReminder = (SimpleReminder) getIntent().getSerializableExtra(ARG_SIMPLE_REMINDER);
            //TODO: restoreSimpleReminder();
        }

        //Add a day to mTomorrow cal
        mTomorrow.add(Calendar.DAY_OF_MONTH, 1);

        setupSpinners();
        setupDateAndTimePickers();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //TODO: Save mNewReminder's data
        //saveSimpleReminder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Restore mNewReminder's data
    }

    private void setupSpinners() {
        reminderCategories = ReminderCategory.getFriendlyValues(this);
        ArrayAdapter reminderCategoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, reminderCategories);
        reminderCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mCategory.setAdapter(reminderCategoryAdapter);

        reminderRepeatTypes = ReminderRepeatType.getFriendlyValues(this);
        ArrayAdapter reminderRepeatTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, reminderRepeatTypes);
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

                                //TODO:Fix this, doesn't work. Also if mRepeatUntilDate < mDateCalPlusOne set to PlusOne.
//                                Calendar mDateCalPlusOne = Calendar.getInstance();
//                                mDateCalPlusOne.set(year, monthOfYear, dayOfMonth);
//                                mDateCalPlusOne.add(Calendar.DAY_OF_MONTH, 1);
//                                mRepeatUntilDatePicker.setDateRange(new MonthAdapter.CalendarDay(mDateCalPlusOne), null);

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

        if(ReminderRepeatType.values()[position] != ReminderRepeatType.DISABLED && !reminderRepeatActive) {
            TransitionManager.beginDelayedTransition(mTransitionsContainer);
            mRepeatContainer.setVisibility(View.VISIBLE);
            reminderRepeatActive = true;

        } else if(ReminderRepeatType.values()[position] == ReminderRepeatType.DISABLED && reminderRepeatActive) {
            TransitionManager.beginDelayedTransition(mTransitionsContainer);
            mRepeatContainer.setVisibility(View.INVISIBLE);
            reminderRepeatActive = false;
        }

        switch(ReminderRepeatType.values()[position]) {
            case DAILY:
                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_days);
                break;
            case WEEKLY:
                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_weeks);
                break;
            case MONTHLY:
                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_months);
                break;
            case YEARLY:
                mRepeatTypeTitle.setText(R.string.activity_reminder_simple_repeat_interval_years);
                break;
        }

        if(ReminderRepeatType.values()[position] != ReminderRepeatType.DISABLED) {
            mRepeatInterval.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

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
                if(valuesAreGood()) {
                    saveSimpleReminderObject();
                    //TODO: startActivityForResult() a AddExtrasActivity() and get extras.
                    //Recover them in onActivityResult() bundle
                    //Also restore reminder data into form
                    //Add a little number on extras menu icon to indicate extras have been added?
                }

                break;
            case R.id.action_save:
                if(valuesAreGood()) {
                    saveSimpleReminderObject();
                    RemindyDAO dao = new RemindyDAO(this);
                    try {
                        dao.insertSimpleReminder(mNewReminder);
                        Snackbar.make(mRepeatContainer, R.string.reminder_saved_successfully, Snackbar.LENGTH_LONG).show();
                    } catch (CouldNotInsertDataException e) {
                        Snackbar.make(mRepeatContainer, R.string.error_problem_inserting_reminder, Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean valuesAreGood() {
        String title = mTitle.getText().toString();
        if(title.trim().isEmpty()) {

            //TODO: add Snackbar with fancy icon!
            //SpannableStringBuilder builder = new SpannableStringBuilder();
            //builder.append( getResources().getString(R.string.error_invalid_title));
            //builder.setSpan(new ImageSpan(SimpleReminderActivity.this, R.drawable.icon_error_snackbar), builder.length() -1, builder.length(), 0);
            //Snackbar.make(mRepeatContainer, builder, Snackbar.LENGTH_LONG).show();
            Snackbar.make(mRepeatContainer, R.string.error_invalid_title, Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(mDateCal == null) {
            Snackbar.make(mRepeatContainer, R.string.error_invalid_date, Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(mTimeTime == null) {
            Snackbar.make(mRepeatContainer, R.string.error_invalid_time, Snackbar.LENGTH_LONG).show();
            mTime.requestFocus();
            return false;
        }

        if(ReminderRepeatType.values()[mRepeatType.getSelectedItemPosition()] != ReminderRepeatType.DISABLED) {
            if(mRepeatInterval.getText().toString().isEmpty()){
                Snackbar.make(mRepeatContainer, R.string.error_invalid_repeat_interval, Snackbar.LENGTH_LONG).show();
                return false;
            }

            if(ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()] == ReminderRepeatEndType.FOR_X_EVENTS) {
                if(mRepeatEndForXEvents.getText().toString().isEmpty()){
                    Snackbar.make(mRepeatContainer, R.string.error_invalid_repeat_events, Snackbar.LENGTH_LONG).show();
                    return false;
                }
            }

            if(ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()] == ReminderRepeatEndType.UNTIL_DATE) {
                if(mRepeatUntilCal == null) {
                    Snackbar.make(mRepeatContainer, R.string.error_invalid_repeat_until_date, Snackbar.LENGTH_LONG).show();
                    return false;
                }
                if(mRepeatUntilCal.compareTo(mDateCal) >= 0) {
                    Snackbar.make(mRepeatContainer, R.string.error_repeat_until_date_after_reminder_date, Snackbar.LENGTH_LONG).show();
                    return false;
                }
            }

        }

        return true;
    }

    private void saveSimpleReminderObject() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        ReminderCategory category = ReminderCategory.values()[mCategory.getSelectedItemPosition()];
        ReminderRepeatType repeatType = ReminderRepeatType.values()[mRepeatType.getSelectedItemPosition()];

        int repeatInterval = 0;
        ReminderRepeatEndType repeatEndType = null;
        int repeatEndNumberOfEvents = 0;

        if(repeatType != ReminderRepeatType.DISABLED) {
            repeatInterval = Integer.parseInt(mRepeatInterval.getText().toString());
            repeatEndType = ReminderRepeatEndType.values()[mRepeatEndType.getSelectedItemPosition()];

            if(repeatEndType == ReminderRepeatEndType.FOR_X_EVENTS)
                repeatEndNumberOfEvents = Integer.parseInt(mRepeatEndForXEvents.getText().toString());

            if(repeatEndType != ReminderRepeatEndType.UNTIL_DATE)
                mRepeatUntilCal = null;
        }
        mNewReminder = new SimpleReminder(ReminderStatus.ACTIVE, title, description, category, mDateCal, mTimeTime, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, mRepeatUntilCal);
    }

}
