package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.EditImageAttachmentActivity;
import ve.com.abicelis.remindy.app.activities.ViewImageAttachmentActivity;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.ImageAttachment;
import ve.com.abicelis.remindy.util.ImageUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class ImageAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    //CONSTS
    private static final String TAG = ImageAttachmentViewHolder.class.getSimpleName();

    //UI
    private LinearLayout mContainer;
    private ImageView mImage;
    private LinearLayout mTapToAddContainer;
    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //DATA
    private ImageAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    public ImageAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_container);
        mImage = (ImageView) itemView.findViewById(R.id.item_attachment_image_content);
        mTapToAddContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_tap_to_add_container);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, ImageAttachment current, int position, boolean realTimeDataPersistence) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;

        mContainer.setOnLongClickListener(this);
        mTapToAddContainer.setOnClickListener(this);
        mImage.setOnClickListener(this);

        setupViewHolder();

        if(mCurrent.getImageFilename() == null)
            launchImageEditAttachmentActivity();
    }

    private void setupViewHolder() {
        if(mCurrent.getImageFilename() == null) {
            mTapToAddContainer.setVisibility(View.VISIBLE);
            mImage.setVisibility(View.GONE);
        } else {
            mTapToAddContainer.setVisibility(View.GONE);
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageBitmap(ImageUtil.getBitmap(mCurrent.getThumbnail()));
        }
    }


    public void setListeners() {
        //Listeners set in setData()
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_tap_to_add_container:
                launchImageEditAttachmentActivity();
                break;

            case R.id.item_attachment_image_content:
                launchImageViewAttachmentActivity();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_container:
                CharSequence items[] = new CharSequence[] {
                        mActivity.getResources().getString(R.string.dialog_image_attachment_options_edit),
                        mActivity.getResources().getString(R.string.dialog_image_attachment_options_delete)};


                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    launchImageEditAttachmentActivity();
                                else if(which == 1)
                                    mAdapter.deleteAttachment(mPosition);
                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;
    }


    public void updateImageAttachment(ImageAttachment imageAttachment){
        mCurrent.setThumbnail(imageAttachment.getThumbnail());
        mCurrent.setImageFilename(imageAttachment.getImageFilename());
        setupViewHolder();
        mAdapter.triggerShowAttachmentHintListener();

        if(mRealTimeDataPersistence)
            mAdapter.triggerAttachmentDataUpdatedListener();
    }

    private void launchImageEditAttachmentActivity() {
        Intent goToEditImageAttachmentActivity = new Intent(mActivity, EditImageAttachmentActivity.class);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
        mActivity.startActivityForResult(goToEditImageAttachmentActivity, EditImageAttachmentActivity.EDIT_IMAGE_ATTACHMENT_REQUEST_CODE);
    }

    private void launchImageViewAttachmentActivity() {

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(mImage, mActivity.getResources().getString(R.string.transition_image_attachment_image));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairs);

        Intent goToViewImageAttachmentActivity = new Intent(mActivity, ViewImageAttachmentActivity.class);
        goToViewImageAttachmentActivity.putExtra(ViewImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
        goToViewImageAttachmentActivity.putExtra(ViewImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
        mActivity.startActivityForResult(goToViewImageAttachmentActivity, ViewImageAttachmentActivity.VIEW_IMAGE_ATTACHMENT_REQUEST_CODE, options.toBundle());
    }

}
