package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderAdapter;

/**
 * Created by abice on 14/3/2017.
 */

public class ReminderHeaderViewHolder extends RecyclerView.ViewHolder {

    //UI
    private TextView mHeaderTitle;

    //DATA
    private ReminderAdapter mAdapter;
    private Activity mActivity;
    private int mPosition;

    public ReminderHeaderViewHolder(View itemView) {
        super(itemView);

        mHeaderTitle = (TextView) itemView.findViewById(R.id.item_reminder_header_title);
    }

    public void setData(ReminderAdapter adapter, Activity activity, String title, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mPosition = position;

        mHeaderTitle.setText(title);
    }
}
