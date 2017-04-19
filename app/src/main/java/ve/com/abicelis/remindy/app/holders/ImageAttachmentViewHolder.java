package ve.com.abicelis.remindy.app.holders;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.EditImageAttachmentActivity;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.ImageAttachment;
import ve.com.abicelis.remindy.util.FileUtil;
import ve.com.abicelis.remindy.util.ImageUtil;
import ve.com.abicelis.remindy.util.PermissionUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

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
    private boolean mCanEdit;

    public ImageAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_container);
        mImage = (ImageView) itemView.findViewById(R.id.item_attachment_image_content);
        mTapToAddContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_tap_to_add_container);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, ImageAttachment current, int position, boolean canEdit) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mCanEdit = canEdit;

        mContainer.setOnLongClickListener(this);
        mTapToAddContainer.setOnClickListener(this);

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
                //TODO: build this activity to see image detail, where user can zoom in
                //Intent viewImageFullscreenIntent = new Intent(mActivity, FUllScreenActivity.class);
                //goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
                //goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
                //mActivity.startActivity(viewImageFullscreenIntent);
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_tap_to_add_container:
                    if(mCanEdit) {
                        CharSequence items[] = new CharSequence[] {
                                mActivity.getResources().getString(R.string.dialog_image_attachment_options_replace),
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
                    }
                return true;
        }
        return false;
    }


    public void updateImageAttachment(ImageAttachment imageAttachment){
        mCurrent = imageAttachment;
        setupViewHolder();
    }

    private void launchImageEditAttachmentActivity() {
        Intent goToEditImageAttachmentActivity = new Intent(mActivity, EditImageAttachmentActivity.class);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
        mActivity.startActivityForResult(goToEditImageAttachmentActivity, EditImageAttachmentActivity.EDIT_IMAGE_ATTACHMENT_REQUEST_CODE);
    }

}
