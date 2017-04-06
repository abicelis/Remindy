package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.ImageAttachment;

/**
 * Created by abice on 13/3/2017.
 */

public class ImageAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private ImageView mImage;
    private LinearLayout mTapToAddContainer;

    //DATA
    private ImageAttachment mCurrent;
    private int mPosition;

    public ImageAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_container);
        mImage = (ImageView) itemView.findViewById(R.id.item_attachment_image_content);
        mTapToAddContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_tap_to_add_container);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, ImageAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;


        if(mCurrent.getThumbnail().length != 0) {
            mTapToAddContainer.setVisibility(View.GONE);
            //TODO: byte array to image bitmap
            //mImage.setImageBitmap(mCurrent.getThumbnail());
        }
    }


    public void setListeners() {
        mTapToAddContainer.setOnClickListener(this);
        mImage.setOnClickListener(this);
        mContainer.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_tap_to_add_container:
                Toast.makeText(mActivity, "ImageAttachment tap to add clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;

            case R.id.item_attachment_image_content:
                Toast.makeText(mActivity, "ImageAttachment image clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_tap_to_add_container:
                Toast.makeText(mActivity, "Tap to add clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_attachment_image_content:
                Toast.makeText(mActivity, "Content image clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_attachment_image_container:
                Toast.makeText(mActivity, "Container LONG clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
