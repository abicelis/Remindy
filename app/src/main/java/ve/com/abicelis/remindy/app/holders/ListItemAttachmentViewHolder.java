package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.adapters.ListItemAttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.EditLinkAttachmentDialogFragment;
import ve.com.abicelis.remindy.app.dialogs.EditListItemAttachmentDialogFragment;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.exception.MalformedLinkException;
import ve.com.abicelis.remindy.model.attachment.ListAttachment;
import ve.com.abicelis.remindy.model.attachment.ListItemAttachment;
import ve.com.abicelis.remindy.util.ClipboardUtil;
import ve.com.abicelis.remindy.util.FileUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class ListItemAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, EditListItemAttachmentDialogFragment.EditListItemAttachmentDialogDismissListener {

    private ListItemAttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private CheckBox mCheckBox;
    private ImageView mTapToAdd;
    private TextView mText;
    private ImageButton mMoreBtn;


    //DATA
    private ListItemAttachment mCurrent;
    private int mPosition;

    public ListItemAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_list_container);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_attachment_list_item_checkbox);
        mTapToAdd = (ImageView) itemView.findViewById(R.id.list_item_attachment_list_item_tap_to_add);
        mText = (TextView) itemView.findViewById(R.id.list_item_attachment_list_item_text);
        mMoreBtn = (ImageButton) itemView.findViewById(R.id.list_item_attachment_list_item_more);
    }


    public void setData(ListItemAttachmentAdapter adapter, Activity activity, ListItemAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        setupViewHolder();
    }

    private void setupViewHolder() {
        mContainer.setOnClickListener(null);
        mMoreBtn.setOnClickListener(null);

        if(mCurrent.getText() == null || mCurrent.getText().isEmpty()) {
            mCheckBox.setVisibility(View.GONE);
            mTapToAdd.setVisibility(View.VISIBLE);

            mMoreBtn.setEnabled(false);
            mMoreBtn.setColorFilter(ContextCompat.getColor(mActivity,  R.color.gray_300));
            mContainer.setOnClickListener(this);

            mText.setText("");
            mCheckBox.setChecked(false);
        } else {
            mCheckBox.setVisibility(View.VISIBLE);
            mTapToAdd.setVisibility(View.GONE);

            mMoreBtn.setEnabled(true);
            mMoreBtn.setColorFilter(ContextCompat.getColor(mActivity,  R.color.primary_dark));
            mMoreBtn.setOnClickListener(this);

            mText.setText(mCurrent.getText());
            mCheckBox.setChecked(mCurrent.isChecked());
        }
    }


    public void setListeners() { /*Done in setData*/ }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_attachment_list_item_more:
                CharSequence items[] = new CharSequence[] {
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_copy),
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_edit),
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_delete)};


                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ClipboardUtil.copyToClipboard(mActivity, mCurrent.getText());
                                        break;

                                    case 1:
                                        handleListItemEdit(false);
                                        break;

                                    case 2:
                                        mAdapter.deleteItem(mPosition);
                                        break;
                                }

                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.list_item_attachment_list_item_checkbox:
                mCurrent.setChecked(!mCurrent.isChecked());
                break;
            case R.id.item_attachment_list_container:
                handleListItemEdit(true);
                break;
        }
    }


    private void handleListItemEdit(boolean isANewItem){
        FragmentManager fm = ((AppCompatActivity)mActivity).getSupportFragmentManager();

        EditListItemAttachmentDialogFragment dialog = EditListItemAttachmentDialogFragment.newInstance(isANewItem, mText.getText().toString());
        dialog.setListener(this);
        dialog.show(fm, "EditLinkAttachmentDialogFragment");
    }

    @Override
    public void onFinishEditListItemAttachmentDialog(String text, boolean isANewItem) {
        mCurrent.setText(text);
        mText.setText(text);
        setupViewHolder();
        if(isANewItem)
            mAdapter.insertNewBlankItem();
    }
}
