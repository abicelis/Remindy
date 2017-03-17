package ve.com.abicelis.remindy.app.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.SimpleReminderActivity;

/**
 * Created by abice on 16/3/2017.
 */

public class NewReminderDialogFragment extends DialogFragment implements View.OnClickListener{

    //UI

    private Button mCancel;
    private Button mNewSimpleReminder;
    private Button mNewAdvancedReminder;

    public NewReminderDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewReminderDialogFragment newInstance() {
        NewReminderDialogFragment frag = new NewReminderDialogFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView =  inflater.inflate(R.layout.dialog_new_reminder, container);

        mCancel = (Button) dialogView.findViewById(R.id.dialog_new_reminder_cancel);
        mNewSimpleReminder = (Button) dialogView.findViewById(R.id.dialog_new_simple_reminder_create);
        mNewAdvancedReminder = (Button) dialogView.findViewById(R.id.dialog_new_advanced_reminder_create);

        mCancel.setOnClickListener(this);
        mNewSimpleReminder.setOnClickListener(this);
        mNewAdvancedReminder.setOnClickListener(this);
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_new_reminder_cancel:
                dismiss();
                break;

            case R.id.dialog_new_simple_reminder_create:
                Intent newSimpleReminderIntent = new Intent(getContext(), SimpleReminderActivity.class);
                startActivity(newSimpleReminderIntent);
                dismiss();
                break;

            case R.id.dialog_new_advanced_reminder_create:
                //Intent newAdvancedReminderIntent = new Intent(getContext(), AdvancedReminderActivity.class);
                //startActivity(newAdvancedReminderIntent);
                dismiss();
                break;
        }
    }
}
