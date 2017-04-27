package ve.com.abicelis.remindy.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import java.util.Calendar;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.interfaces.TaskDataInterface;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.util.CalendarUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;

/**
 * Created by abice on 20/4/2017.
 */

public class EditOneTimeReminderFragment extends Fragment implements TaskDataInterface {

    //CONST
    public static final String REMINDER_ARGUMENT = "REMINDER_ARGUMENT";

    //DATA
    OneTimeReminder mReminder;
    Calendar mDateCal;
    Time mTimeTime;
    DateFormat mDateFormat;

    //UI
    private EditText mDate;
    private EditText mTime;
    private CalendarDatePickerDialogFragment mDatePicker;

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
            mReminder = (OneTimeReminder) getArguments().getSerializable(REMINDER_ARGUMENT);
        else
            Toast.makeText(getActivity(), getResources().getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();


        mDateFormat = SharedPreferenceUtil.getDateFormat(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_one_time_reminder, container, false);

        mDate = (EditText) rootView.findViewById(R.id.fragment_edit_one_time_reminder_date);
        mTime = (EditText) rootView.findViewById(R.id.fragment_edit_one_time_reminder_time);

        setupDateAndTimePickers();
        setReminderValues();

        return rootView;
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
                                    mDateCal = CalendarUtil.getNewInstanceZeroedCalendar();
                                }
                                mDateCal.set(year, monthOfYear, dayOfMonth);
                                mDate.setText(mDateFormat.formatCalendar(mDateCal));
                                mReminder.setDate(mDateCal);
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
                                mReminder.setTime(mTimeTime);
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
            mDateCal = CalendarUtil.getNewInstanceZeroedCalendar();
            mDateCal.setTimeInMillis(mReminder.getDate().getTimeInMillis());
            mDate.setText(mDateFormat.formatCalendar(mDateCal));
            mDatePicker.setPreselectedDate(mDateCal.get(Calendar.YEAR), mDateCal.get(Calendar.MONTH), mDateCal.get(Calendar.DAY_OF_MONTH));
        }

        if(mReminder.getTime() != null) {
            mTimeTime = mReminder.getTime();
            mDateCal.setTimeInMillis(mReminder.getDate().getTimeInMillis());
            mDate.setText(mDateFormat.formatCalendar(mDateCal));
            mDatePicker.setPreselectedDate(mDateCal.get(Calendar.YEAR), mDateCal.get(Calendar.MONTH), mDateCal.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Override
    public void updateData() {
        //Date, Time already set!
    }

}
