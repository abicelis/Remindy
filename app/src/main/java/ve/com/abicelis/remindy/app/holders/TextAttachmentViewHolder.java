package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.EditTextAttachmentDialogFragment;
import ve.com.abicelis.remindy.model.attachment.TextAttachment;
import ve.com.abicelis.remindy.util.ClipboardUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class TextAttachmentViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, View.OnLongClickListener, EditTextAttachmentDialogFragment.EditTextAttachmentDialogDismissListener {

    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private TextView mText;

    //DATA
    private TextAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    public TextAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_text_container);
        mText = (TextView) itemView.findViewById(R.id.item_attachment_text_content);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, TextAttachment current, int position, boolean realTimeDataPersistence) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;

        if(current.getText() != null && !current.getText().isEmpty())
            mText.setText(mCurrent.getText());
        else
            handleTextEdit();
    }


    public void setListeners() {
        mText.setOnClickListener(this);
        mContainer.setOnLongClickListener(this);
    }



    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_text_content:
                if(mText.getText().equals(""))
                    handleTextEdit();
                else
                    handleTextCopy();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_text_container:
                CharSequence items[] = new CharSequence[]{
                            mActivity.getResources().getString(R.string.dialog_text_attachment_options_copy),
                            mActivity.getResources().getString(R.string.dialog_text_attachment_options_edit),
                            mActivity.getResources().getString(R.string.dialog_text_attachment_options_delete)};

                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        handleTextCopy();
                                        break;
                                    case 1:
                                        handleTextEdit();
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

    private void handleTextCopy() {
        ClipboardUtil.copyToClipboard(mActivity, mCurrent.getText());
    }

    private void handleTextEdit(){
        FragmentManager fm = ((AppCompatActivity)mActivity).getSupportFragmentManager();

        EditTextAttachmentDialogFragment dialog = EditTextAttachmentDialogFragment.newInstance(mText.getText().toString());
        dialog.setListener(this);
        dialog.show(fm, "EditTextAttachmentDialogFragment");
    }

    @Override
    public void onFinishEditTextAttachmentDialog(String text) {
        mText.setText(text);
        mCurrent.setText(text);
        mAdapter.triggerShowAttachmentHintListener();

        if(mRealTimeDataPersistence)
            mAdapter.triggerAttachmentDataUpdatedListener();
    }

}
