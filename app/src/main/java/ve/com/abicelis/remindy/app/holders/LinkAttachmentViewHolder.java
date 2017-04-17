package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.EditLinkAttachmentDialogFragment;
import ve.com.abicelis.remindy.exception.MalformedLinkException;
import ve.com.abicelis.remindy.model.attachment.LinkAttachment;
import ve.com.abicelis.remindy.util.ClipboardUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class LinkAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, EditLinkAttachmentDialogFragment.EditLinkAttachmentDialogDismissListener {

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

        if(current.getLink() != null && !current.getLink().isEmpty())
            mLink.setText(mCurrent.getLink());
        else
            handleLinkEdit();
    }


    public void setListeners() {
        mContainer.setOnClickListener(this);
        mContainer.setOnLongClickListener(this);
    }



    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_link_container:
                handleLinkEdit();
        }
    }

    @Override
    public boolean onLongClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_link_container:
                CharSequence items[] = new CharSequence[]{
                        mActivity.getResources().getString(R.string.dialog_link_attachment_options_copy),
                        mActivity.getResources().getString(R.string.dialog_link_attachment_options_edit),
                        mActivity.getResources().getString(R.string.dialog_link_attachment_options_delete)};


                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        ClipboardUtil.copyToClipboard(mActivity, mCurrent.getLink());

                                        break;
                                    case 1:
                                        handleLinkEdit();
                                        break;
                                    case 2:
                                        mAdapter.deleteAttachment(mPosition);
                                        break;
                                }
                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;

    }

    private void handleLinkEdit(){
        FragmentManager fm = ((AppCompatActivity)mActivity).getSupportFragmentManager();

        EditLinkAttachmentDialogFragment dialog = EditLinkAttachmentDialogFragment.newInstance(mLink.getText().toString());
        dialog.setListener(this);
        dialog.show(fm, "EditLinkAttachmentDialogFragment");
    }

    @Override
    public void onFinishEditLinkAttachmentDialog(String text) {
        try {
            mCurrent.setLink(text);
            mLink.setText(text);
            mAdapter.triggerShowAttachmentHintListener();
        } catch (MalformedLinkException e) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.dialog_edit_link_attachment_malformed_link), Toast.LENGTH_SHORT).show();
        }
    }
}
