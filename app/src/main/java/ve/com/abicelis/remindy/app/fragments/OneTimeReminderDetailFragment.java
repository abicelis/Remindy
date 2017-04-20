package ve.com.abicelis.remindy.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class OneTimeReminderDetailFragment extends Fragment {

    //CONST
    public static final String REMINDER_TO_DISPLAY = "REMINDER_TO_DISPLAY";

    //DATA
    private OneTimeReminder mReminder;

    //UI
    private LinearLayout mContainer;
    private ImageView mDateIcon;
    private TextView mDate;
    private TextView mTime;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments().containsKey(REMINDER_TO_DISPLAY)) {
            mReminder = (OneTimeReminder) getArguments().getSerializable(REMINDER_TO_DISPLAY);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.fragment_location_based_reminder_detail_snackbar_error_no_reminder, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_reminder_one_time, container, false);

        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_one_time_container);
        mDateIcon = (ImageView) rootView.findViewById(R.id.fragment_reminder_one_time_date_icon);
        mDate = (TextView) rootView.findViewById(R.id.fragment_reminder_one_time_date);
        mTime = (TextView) rootView.findViewById(R.id.fragment_reminder_one_time_time);

        DateFormat df = SharedPreferenceUtil.getDateFormat(getActivity());
        mDate.setText(df.formatCalendar(mReminder.getDate()));                  //TODO: Set date icon color to red if task is overdue
        mTime.setText(mReminder.getTime().toString());

        return rootView;
    }


}
