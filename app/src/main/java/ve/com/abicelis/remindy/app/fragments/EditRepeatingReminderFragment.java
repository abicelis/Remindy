package ve.com.abicelis.remindy.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.transitionseverywhere.TransitionManager;

import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.interfaces.TaskDataInterface;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.CalendarUtil;
import ve.com.abicelis.remindy.util.InputFilterMinMax;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;

/**
 * Created by abice on 20/4/2017.
 */

public class EditRepeatingReminderFragment extends Fragment implements TaskDataInterface {

    //CONST
    public static final String REMINDER_ARGUMENT = "REMINDER_ARGUMENT";
    //public static final String INSTANCE_STATE_REMINDER_KEY = "INSTANCE_STATE_REMINDER_KEY";


    //DATA
    private List<String> reminderRepeatTypes;
    private List<String> reminderRepeatEndTypes;
    RepeatingReminder mReminder;
    DateFormat mDateFormat;


    //UI
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        //If a state was saved (such as when rotating the device), restore the state!
//        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_REMINDER_KEY)) {
//            mReminder = (RepeatingReminder) savedInstanceState.getSerializable(INSTANCE_STATE_REMINDER_KEY);
//        }


        //If fragment was just called, expect a reminder at REMINDER_ARGUMENT
        if(getArguments().containsKey(REMINDER_ARGUMENT))
            mReminder = (RepeatingReminder) getArguments().getSerializable(REMINDER_ARGUMENT);
        else
            Toast.makeText(getActivity(), getResources().getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();


        mDateFormat = SharedPreferenceUtil.getDateFormat(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_repeating_reminder, container, false);

        mDate = (EditText) rootView.findViewById(R.id.fragment_edit_repeating_reminder_date);
        mTime = (EditText) rootView.findViewById(R.id.fragment_edit_repeating_reminder_time);
        mRepeatType = (Spinner) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_type);
        mTransitionsContainer = (LinearLayout) rootView.findViewById(R.id.fragment_edit_repeating_reminder_transitions_container);
        mRepeatContainer = (LinearLayout) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_container);
        mRepeatInterval = (EditText) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_interval);
        mRepeatTypeTitle = (TextView) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_type_title);
        mRepeatEndForEventsContainer = (LinearLayout) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_end_for_events_container);
        mRepeatEndUntilContainer = (LinearLayout) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_end_until_container);
        mRepeatEndForXEvents = (EditText) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_for_x_events);
        mRepeatEndType = (Spinner) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_end_type);
        mRepeatUntilDate = (EditText) rootView.findViewById(R.id.fragment_edit_repeating_reminder_repeat_until);

        mRepeatInterval.setFilters(new InputFilter[]{new InputFilterMinMax("1", "99")});
        mRepeatEndForXEvents.setFilters(new InputFilter[]{new InputFilterMinMax("1", "99")});

        mDatePicker = new CalendarDatePickerDialogFragment();
        mRepeatUntilDatePicker = new CalendarDatePickerDialogFragment();

        setupSpinners();
        setupDateAndTimePickers();
        setReminderValues();

        return rootView;
    }


    private void setupSpinners() {
        reminderRepeatTypes = ReminderRepeatType.getFriendlyValues(getActivity());
        ArrayAdapter reminderRepeatTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, reminderRepeatTypes);
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

        reminderRepeatEndTypes = ReminderRepeatEndType.getFriendlyValues(getActivity());
        ArrayAdapter reminderRepeatEndTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, reminderRepeatEndTypes);
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
                mDatePicker.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(mReminder.getDate() == null) {
                                    mReminder.setDate(CalendarUtil.getNewInstanceZeroedCalendar());
                                }
                                mReminder.getDate().set(year, monthOfYear, dayOfMonth);
                                mDate.setText(mDateFormat.formatCalendar(mReminder.getDate()));
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), mToday.get(Calendar.DAY_OF_MONTH))
                        .setDateRange(new MonthAdapter.CalendarDay(mToday), null)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                mDatePicker.show(getFragmentManager(), "mDate");
            }
        });

        mRepeatUntilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepeatUntilDatePicker.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(mReminder.getRepeatEndDate() == null) {
                                    mReminder.setRepeatEndDate(CalendarUtil.getNewInstanceZeroedCalendar());
                                }
                                mReminder.getRepeatEndDate().set(year, monthOfYear, dayOfMonth);
                                mRepeatUntilDate.setText(mDateFormat.formatCalendar(mReminder.getRepeatEndDate()));
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(mTomorrow.get(Calendar.YEAR), mTomorrow.get(Calendar.MONTH), mTomorrow.get(Calendar.DAY_OF_MONTH))
                        .setDateRange(new MonthAdapter.CalendarDay(mTomorrow), null)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                mRepeatUntilDatePicker.show(getFragmentManager(), "mRepeatUntilDate");
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                if(mReminder.getTime() == null) {
                                    mReminder.setTime(new Time());
                                    //TODO: grab timeFormat from preferences and mTimeTime.setDisplayTimeFormat();
                                }
                                mReminder.getTime().setHour(hourOfDay);
                                mReminder.getTime().setMinute(minute);
                                mTime.setText(mReminder.getTime().toString());
                            }
                        })
                        .setStartTime(12, 0)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                rtpd.show(getFragmentManager(), "mTime");
            }
        });
    }


    private void setReminderValues() {
        if(mReminder.getDate() != null) {
            mDate.setText(mDateFormat.formatCalendar(mReminder.getDate()));
            //mDatePicker.setPreselectedDate(mReminder.getDate().get(Calendar.YEAR), mReminder.getDate().get(Calendar.MONTH), mReminder.getDate().get(Calendar.DAY_OF_MONTH));
        }

        if(mReminder.getTime() != null) {
            mTime.setText(mReminder.getTime().toString());
            //mTimePicker.setStartTime(mReminder.getTime().getHour(), mReminder.getTime().getMinute());
        }

        if(mReminder.getRepeatType() != null) {
            mRepeatType.setSelection(mReminder.getRepeatType().ordinal());
        }

        mRepeatInterval.setText(String.valueOf(mReminder.getRepeatInterval()));

        if(mReminder.getRepeatEndType() != null) {
            mRepeatEndType.setSelection(mReminder.getRepeatEndType().ordinal());

            switch (mReminder.getRepeatEndType()) {
                case UNTIL_DATE:
                    if(mReminder.getRepeatEndDate() != null) {
                        mRepeatUntilDate.setText(mDateFormat.formatCalendar(mReminder.getRepeatEndDate()));
                        //mRepeatUntilDatePicker.setPreselectedDate(mReminder.getRepeatEndDate().get(Calendar.YEAR), mReminder.getRepeatEndDate().get(Calendar.MONTH), mReminder.getRepeatEndDate().get(Calendar.DAY_OF_MONTH));
                    }
                    break;
                case FOR_X_EVENTS:
                    mRepeatEndForXEvents.setText(String.valueOf(mReminder.getRepeatEndNumberOfEvents()));
            }
        }

    }

    private void handleRepeatTypeSelected(int position) {

        mReminder.setRepeatType(ReminderRepeatType.values()[position]);
        switch(mReminder.getRepeatType()) {
            case DAILY:
                mRepeatTypeTitle.setText(R.string.fragment_edit_repeating_reminder_repeat_interval_days);
                break;
            case WEEKLY:
                mRepeatTypeTitle.setText(R.string.fragment_edit_repeating_reminder_repeat_interval_weeks);
                break;
            case MONTHLY:
                mRepeatTypeTitle.setText(R.string.fragment_edit_repeating_reminder_repeat_interval_months);
                break;
            case YEARLY:
                mRepeatTypeTitle.setText(R.string.fragment_edit_repeating_reminder_repeat_interval_years);
                break;
        }

        mRepeatInterval.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void handleRepeatEndTypeSelected(int position) {
        mReminder.setRepeatEndType(ReminderRepeatEndType.values()[position]);

        switch (mReminder.getRepeatEndType()) {
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
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
        }
    }

    @Override
    public void updateData() {

        //Date, Time, RepeatType, RepeatEndType and RepeatEndDate already set
        try {
            mReminder.setRepeatInterval(Integer.parseInt(mRepeatInterval.getText().toString()));
        }catch (NumberFormatException e) {
            mReminder.setRepeatInterval(-1);
        }

        switch (mReminder.getRepeatEndType()){
            case FOR_X_EVENTS:
                mReminder.setRepeatEndDate(null);
                try {
                    mReminder.setRepeatEndNumberOfEvents(Integer.parseInt(mRepeatEndForXEvents.getText().toString()));
                }catch (NumberFormatException e) {
                    mReminder.setRepeatEndNumberOfEvents(-1);
                }
                break;
            case FOREVER:
                mReminder.setRepeatEndDate(null);
                mReminder.setRepeatEndNumberOfEvents(0);
                break;
            case UNTIL_DATE:
                mReminder.setRepeatEndNumberOfEvents(0);
                break;
        }
    }

}
