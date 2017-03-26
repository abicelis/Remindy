package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.TaskAdapter;

/**
 * Created by abice on 14/3/2017.
 */

public class TaskHeaderViewHolder extends RecyclerView.ViewHolder {

    //UI
    private TextView mHeaderTitle;

    //DATA
    private TaskAdapter mAdapter;
    private Activity mActivity;
    private int mPosition;

    public TaskHeaderViewHolder(View itemView) {
        super(itemView);

        mHeaderTitle = (TextView) itemView.findViewById(R.id.item_task_header_title);
    }

    public void setData(TaskAdapter adapter, Activity activity, String title, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mPosition = position;

        mHeaderTitle.setText(title);
    }
}
