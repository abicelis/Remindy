package ve.com.abicelis.remindy.app.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.holders.AdvancedReminderViewHolder;
import ve.com.abicelis.remindy.app.holders.ProgrammedLocationBasedTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.ProgrammedOneTimeTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.ProgrammedRepeatingTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.TaskHeaderViewHolder;
import ve.com.abicelis.remindy.app.holders.UnprogrammedTaskViewHolder;
import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 13/3/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //DATA
    private List<TaskViewModel> mTasks;
    private LayoutInflater mInflater;
    private Activity mActivity;

    public TaskAdapter(Activity activity, List<TaskViewModel> tasks) {
        mTasks = tasks;
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);

    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.get(position).getViewModelType().ordinal();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        TaskViewModelType viewModelType = TaskViewModelType.values()[viewType];
        switch (viewModelType) {
            case HEADER:
                return new TaskHeaderViewHolder(mInflater.inflate(R.layout.list_item_task_header, parent, false));
            case UNPROGRAMMED_REMINDER:
                return new UnprogrammedTaskViewHolder(mInflater.inflate(R.layout.list_item_task_unprogrammed, parent, false));
            case ONE_TIME_REMINDER:
                return new ProgrammedOneTimeTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_one_time, parent, false));
            case REPEATING_REMINDER:
                return new ProgrammedRepeatingTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_repeating, parent, false));
            case LOCATION_BASED_REMINDER:
                return new ProgrammedLocationBasedTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_location_based, parent, false));
            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in TaskAdapter");
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TaskViewModelType viewModelType = mTasks.get(position).getViewModelType();
        switch (viewModelType) {
            case HEADER:
                TaskHeaderViewHolder thHolder = (TaskHeaderViewHolder) holder;
                thHolder.setData(this, mActivity, mTasks.get(position).getHeaderTitle(), mTasks.get(position).isHeaderTitleRed(), position);
                break;

            case UNPROGRAMMED_REMINDER:
                UnprogrammedTaskViewHolder uHolder = (UnprogrammedTaskViewHolder) holder;
                uHolder.setData(this, mActivity, mTasks.get(position).getTask(), position);
                uHolder.setListeners();
                break;

            case ONE_TIME_REMINDER:
                ProgrammedOneTimeTaskViewHolder poHolder = (ProgrammedOneTimeTaskViewHolder) holder;
                poHolder.setData(this, mActivity, mTasks.get(position).getTask(), position);
                poHolder.setListeners();
                break;

            case REPEATING_REMINDER:
                ProgrammedRepeatingTaskViewHolder prHolder = (ProgrammedRepeatingTaskViewHolder) holder;
                prHolder.setData(this, mActivity, mTasks.get(position).getTask(), position);
                prHolder.setListeners();
                break;

            case LOCATION_BASED_REMINDER:
                ProgrammedLocationBasedTaskViewHolder plHolder = (ProgrammedLocationBasedTaskViewHolder) holder;
                plHolder.setData(this, mActivity, mTasks.get(position).getTask(), position);
                plHolder.setListeners();
                break;

            default:
                throw new InvalidParameterException("Wrong viewType passed to onBindViewHolder in TaskAdapter");
        }

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }
}
