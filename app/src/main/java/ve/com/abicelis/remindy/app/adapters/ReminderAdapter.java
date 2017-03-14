package ve.com.abicelis.remindy.app.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.holders.ReminderViewHolder;
import ve.com.abicelis.remindy.model.Reminder;

/**
 * Created by abice on 13/3/2017.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder> {

    //DATA
    private List<Reminder> mReminders;
    private LayoutInflater mInflater;
    private Activity mActivity;

    public ReminderAdapter(Activity activity, List<Reminder> reminders) {
        mReminders = reminders;
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);

    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder current = mReminders.get(position);
        holder.setData(this, mActivity, current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }
}
