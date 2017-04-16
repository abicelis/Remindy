package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.TaskDetailActivity;
import ve.com.abicelis.remindy.app.adapters.TaskAdapter;
import ve.com.abicelis.remindy.app.fragments.TaskListFragment;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.enums.TaskCategory;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.attachment.Attachment;

/**
 * Created by abice on 13/3/2017.
 */

public class UnprogrammedTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TaskAdapter mAdapter;
    private Fragment mFragment;

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

    private View mItemDecoration;

    //DATA
    private Task mCurrent;
    private int mReminderPosition;

    public UnprogrammedTaskViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_task_unprogrammed_container);
        mCategoryIcon = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_category_icon);

        mAttachmentList = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_attachment_list);
        mAttachmentLink = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_attachment_link);
        mAttachmentAudio = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_attachment_audio);
        mAttachmentImage = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_attachment_image);
        mAttachmentText = (ImageView) itemView.findViewById(R.id.item_task_unprogrammed_attachment_text);

        mTitle = (TextView) itemView.findViewById(R.id.item_task_unprogrammed_title);
        mDescription = (TextView) itemView.findViewById(R.id.item_task_unprogrammed_description);
        mItemDecoration = itemView.findViewById(R.id.item_task_unprogrammed_item_decoration);

    }


    public void setData(TaskAdapter adapter, Fragment fragment, Task current, int position, boolean nextItemIsATask) {
        mAdapter = adapter;
        mFragment = fragment;
        mCurrent = current;
        mReminderPosition = position;

        mCategoryIcon.setImageResource(mCurrent.getCategory().getIconRes());

        mAttachmentList.setColorFilter(ContextCompat.getColor(mFragment.getActivity(), (hasExtrasOfType(AttachmentType.LIST) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentLink.setColorFilter(ContextCompat.getColor(mFragment.getActivity(), (hasExtrasOfType(AttachmentType.LINK) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentAudio.setColorFilter(ContextCompat.getColor(mFragment.getActivity(), (hasExtrasOfType(AttachmentType.AUDIO) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentImage.setColorFilter(ContextCompat.getColor(mFragment.getActivity(), (hasExtrasOfType(AttachmentType.IMAGE) ? R.color.icons_enabled : R.color.icons_disabled)));
        mAttachmentText.setColorFilter(ContextCompat.getColor(mFragment.getActivity(), (hasExtrasOfType(AttachmentType.TEXT) ? R.color.icons_enabled : R.color.icons_disabled)));

        mTitle.setText(mCurrent.getTitle());
        if(!mCurrent.getDescription().isEmpty())
            mDescription.setText(mCurrent.getDescription());
        else
            mDescription.setText("-");

        mItemDecoration.setVisibility(nextItemIsATask ? View.VISIBLE : View.INVISIBLE);
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
            case R.id.item_task_unprogrammed_container:
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(mCategoryIcon, mFragment.getResources().getString(R.string.transition_task_list_category));
                //pairs[1] = new Pair<View, String>(mTitle, mFragment.getResources().getString(R.string.transition_task_list_title));
                //pairs[2] = new Pair<View, String>(mDescription, mFragment.getResources().getString(R.string.transition_task_list_description));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);

                Intent openTaskDetailActivity = new Intent(mFragment.getActivity(), TaskDetailActivity.class);
                openTaskDetailActivity.putExtra(TaskDetailActivity.TASK_TO_DISPLAY, mCurrent);
                openTaskDetailActivity.putExtra(TaskListFragment.TASK_DETAIL_RETURN_TASK_POSITION, mReminderPosition);
                mFragment.startActivityForResult(openTaskDetailActivity, TaskListFragment.TASK_DETAIL_REQUEST_CODE, options.toBundle());
                break;
        }
    }

}
