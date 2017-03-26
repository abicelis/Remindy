package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.TaskAdapter;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.exception.WrongReminderTypeException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.attachment.Attachment;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class ProgrammedOneTimeTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TaskAdapter mAdapter;
    private Activity mActivity;

    //UI
    private RelativeLayout mContainer;
    private ImageView mCategoryIcon;

    private ImageView mAttachmentList;
    private ImageView mAttachmentLink;
    private ImageView mAttachmentAudio;
    private ImageView mAttachmentImage;
    private ImageView mAttachmentText;

    private TextView mTitle;
    private TextView mDescription;
    private TextView mDate;
    private TextView mTime;


    //DATA
    private Task mCurrent;
    private int mReminderPosition;

    public ProgrammedOneTimeTaskViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_task_programmed_one_time_container);
        mCategoryIcon = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_category_icon);

        mAttachmentList = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_attachment_list);
        mAttachmentLink = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_attachment_link);
        mAttachmentAudio = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_attachment_audio);
        mAttachmentImage = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_attachment_image);
        mAttachmentText = (ImageView) itemView.findViewById(R.id.item_task_programmed_one_time_attachment_text);

        mTitle = (TextView) itemView.findViewById(R.id.item_task_programmed_one_time_title);
        mDescription = (TextView) itemView.findViewById(R.id.item_task_programmed_one_time_description);

        mDate = (TextView) itemView.findViewById(R.id.item_task_programmed_one_time_date);
        mTime = (TextView) itemView.findViewById(R.id.item_task_programmed_one_time_time);

    }


    public void setData(TaskAdapter adapter, Activity activity, Task current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mReminderPosition = position;

        mCategoryIcon.setImageResource(mCurrent.getCategory().getIconRes());

        mAttachmentList.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.LIST) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentLink.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.LINK) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentAudio.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.AUDIO) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentImage.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.IMAGE) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentText.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.TEXT) ? R.color.icons_enabled : R.color.icons_disabled)));

        mTitle.setText(mCurrent.getTitle());
        if(!mCurrent.getDescription().isEmpty())
            mDescription.setText(mCurrent.getDescription());
        else
            mDescription.setText("-");

        if(current.getReminderType() == ReminderType.ONE_TIME && current.getReminder() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            mDate.setText(formatter.format(((OneTimeReminder)current.getReminder()).getDate().getTime()));
            mTime.setText(((OneTimeReminder)current.getReminder()).getTime().toString());
        } else {
            mDate.setText("-");
            mTime.setText("-");
        }
    }


    private boolean hasExtrasOfType(AttachmentType extraType) {
        for (Attachment extra : mCurrent.getAttachments()) {
            if(extra.getType().equals(extraType))
                return true;
        }
        return false;
    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_task_programmed_one_time_container:
                Toast.makeText(mActivity, "One-time reminder Task '" + mCurrent.getTitle() + "' clicked! Pos=" + mReminderPosition, Toast.LENGTH_SHORT).show();
//                Pair[] pairs = new Pair[1];
//                pairs[0] = new Pair<View, String>(mImage, mFragment.getResources().getString(R.string.transition_name_expense_detail_image));
//                //pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
//                //pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
//                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));
//
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);
//                Intent expenseDetailIntent = new Intent(mFragment.getActivity(), ExpenseDetailActivity.class);
//                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_EXPENSE, mCurrent);
//                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_CREDIT_PERIOD_ID, mCreditPeriodId);
//                mFragment.startActivityForResult(expenseDetailIntent, Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());

                break;
        }
    }

}
