package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.EditLinkAttachmentDialogFragment;
import ve.com.abicelis.remindy.app.dialogs.EditTextAttachmentDialogFragment;
import ve.com.abicelis.remindy.exception.MalformedLinkException;
import ve.com.abicelis.remindy.model.attachment.LinkAttachment;

/**
 * Created by abice on 13/3/2017.
 */

public class LinkAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, EditLinkAttachmentDialogFragment.EditLinkAttachmentDialogDismissListener {

    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private TextView mLink;

    //DATA
    private LinkAttachment mCurrent;
    private int mPosition;

    public LinkAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_link_container);
        mLink = (TextView) itemView.findViewById(R.id.item_attachment_link_content);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, LinkAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        mLink.setText(mCurrent.getLink());
    }


    public void setListeners() {
        mContainer.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_link_container:
                handleTextEdit();
        }
    }

    private void handleTextEdit(){
        FragmentManager fm = ((AppCompatActivity)mActivity).getSupportFragmentManager();

        EditLinkAttachmentDialogFragment dialog = EditLinkAttachmentDialogFragment.newInstance(mLink.getText().toString());
        dialog.setListener(this);
        dialog.show(fm, "EditTextAttachmentDialogFragment");
    }

    @Override
    public void onFinishEditLinkAttachmentDialog(String text) {
        try {
            mCurrent.setLink(text);
            mLink.setText(text);
        } catch (MalformedLinkException e) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.dialog_edit_link_attachment_malformed_link), Toast.LENGTH_SHORT).show();
        }
    }
}
