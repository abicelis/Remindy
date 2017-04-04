package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.TaskAdapter;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.attachment.Attachment;

/**
 * Created by abice on 13/3/2017.
 */

public class AdvancedReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TaskAdapter mAdapter;
    private Activity mActivity;

    //UI
    private RelativeLayout mContainer;
    private ImageView mReminderIconBackground;
    private TextView mReminderIconText;
    private ImageView mCategory;
    private TextView mTitle;
    private TextView mDescription;
    private ImageView mExtraLink;
    private ImageView mExtraAudio;
    private ImageView mExtraImage;
    private ImageView mExtraText;
    private ImageView mTimeIcon;
    private TextView mTime;
    private ImageView mAddressIcon;
    private TextView mAddress;

    //DATA
    private Task mCurrent;
    private int mReminderPosition;

    public AdvancedReminderViewHolder(View itemView) {
        super(itemView);

//        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_reminder_container);
//        mReminderIconBackground = (ImageView) itemView.findViewById(R.id.item_reminder_icon_background);
//        mReminderIconText = (TextView) itemView.findViewById(R.id.item_reminder_icon_text);
//        mCategory = (ImageView) itemView.findViewById(R.id.item_reminder_category);
//        mTitle = (TextView) itemView.findViewById(R.id.item_reminder_title);
//        mDescription = (TextView) itemView.findViewById(R.id.item_reminder_description);
//        mExtraLink = (ImageView) itemView.findViewById(R.id.item_reminder_extra_link);
//        mExtraAudio = (ImageView) itemView.findViewById(R.id.item_reminder_extra_audio);
//        mExtraImage = (ImageView) itemView.findViewById(R.id.item_reminder_extra_image);
//        mExtraText = (ImageView) itemView.findViewById(R.id.item_reminder_extra_text);
//        mTimeIcon = (ImageView) itemView.findViewById(R.id.item_reminder_time_icon);
//        mTime = (TextView) itemView.findViewById(R.id.item_reminder_time);
//        mAddressIcon = (ImageView) itemView.findViewById(R.id.item_reminder_address_icon);
//        mAddress = (TextView) itemView.findViewById(R.id.item_reminder_address);
    }


    public void setData(TaskAdapter adapter, Activity activity, Task current, int position) {
//        mAdapter = adapter;
//        mActivity = activity;
//        mCurrent = current;
//        mReminderPosition = position;
//
//        //TODO: do something fancy with mReminderBackground color
//        mReminderIconBackground.setColorFilter(getReminderColor());
//
//
//        mReminderIconText.setText(getIconTextFromReminderTitle());
//        mCategory.setImageResource((mCurrent.getCategory() == TaskCategory.BUSINESS ? R.drawable.icon_business : R.drawable.icon_personal));
//        mTitle.setText(mCurrent.getTitle());
//        if(!mCurrent.getDescription().isEmpty())
//            mDescription.setText(mCurrent.getDescription());
//        else
//            mDescription.setText("-");
//
//        mExtraLink.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.LINK) ? R.color.icons_enabled : R.color.icons_disabled)));
//        mExtraAudio.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.AUDIO) ? R.color.icons_enabled : R.color.icons_disabled)));
//        mExtraImage.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.IMAGE) ? R.color.icons_enabled : R.color.icons_disabled)));
//        mExtraText.setColorFilter(ContextCompat.getColor(mActivity, (hasExtrasOfType(AttachmentType.TEXT) ? R.color.icons_enabled : R.color.icons_disabled)));

//        switch (mCurrent.getTimeType()) {
//            case ANYTIME:
//                mTime.setText(R.string.reminder_time_type_anytime);
//                mTimeIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.icons_disabled));
//                mTime.setTextColor(ContextCompat.getColor(mActivity, R.color.icons_disabled));
//                break;
//            case SINGLE_TIME:
//                mTime.setText(mCurrent.getStartTime().toString());
//                break;
//            case INTERVAL:
//                mTime.setText(mCurrent.getStartTime().toString() + " - " + mCurrent.getEndTime().toString());
//                break;
//        }
//
//        if(mCurrent.getPlace() != null)
//            mAddress.setText(mCurrent.getPlace().getAddress());
//        else{
//            mAddressIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.icons_disabled));
//            mAddress.setTextColor(ContextCompat.getColor(mActivity, R.color.icons_disabled));
//            mAddress.setText(R.string.fragment_reminder_list_place_anywhere);
//        }
    }

    private String getIconTextFromReminderTitle() {
        String title = mCurrent.getTitle();

        if(title.isEmpty())
            return "--";

        String[] words = title.split(" ", 2);
        if(words.length == 1)
            return words[0].substring(0, 1).toUpperCase();
        else
            return words[0].substring(0, 1).toUpperCase() + words[1].substring(0, 1).toLowerCase();
    }

    private boolean hasExtrasOfType(AttachmentType extraType) {
        for (Attachment extra : mCurrent.getAttachments()) {
            if(extra.getType().equals(extraType))
                return true;
        }
        return false;
    }

    private int getReminderColor() {
        int colorPos = mReminderPosition % 5;

        switch (colorPos) {
            case 0:
                return ContextCompat.getColor(mActivity, R.color.category_health);
            case 1:
                return ContextCompat.getColor(mActivity, R.color.category_business);
            case 2:
                return ContextCompat.getColor(mActivity, R.color.category_repairs);
            case 3:
                return ContextCompat.getColor(mActivity, R.color.category_personal);
            case 4:
                return ContextCompat.getColor(mActivity, R.color.category_shopping);
            default:
                return ContextCompat.getColor(mActivity, R.color.category_health);

        }
    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_reminder_container:
                Toast.makeText(mActivity, "ADV Task '" + mCurrent.getTitle() + "' clicked! Pos=" + mReminderPosition, Toast.LENGTH_SHORT).show();
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
