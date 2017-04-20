package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.adapters.ListItemAttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.ListAttachment;
import ve.com.abicelis.remindy.model.attachment.ListItemAttachment;
import ve.com.abicelis.remindy.util.ClipboardUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class ListAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private AttachmentAdapter mAttachmentAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListItemAttachmentAdapter mListItemAdapter;

    //DATA
    private ListAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    public ListAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_list_container);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_attachment_list_recycler);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, ListAttachment current, int position, boolean realTimeDataPersistence) {
        mAttachmentAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;

        setUpRecyclerView();
        mAttachmentAdapter.triggerShowAttachmentHintListener();
    }


    public void setListeners() {
        mContainer.setOnLongClickListener(this);
    }

    private void setUpRecyclerView() {
        List<ListItemAttachment> items = mCurrent.getItems();

        //if(items.size() == 0)                       //List is empty, add a blank item
            items.add(new ListItemAttachment());

        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mListItemAdapter = new ListItemAttachmentAdapter(mActivity, items, mRealTimeDataPersistence);
        mListItemAdapter.setAttachmentDataUpdatedListener(new ListItemAttachmentAdapter.AttachmentDataUpdatedListener() {
            @Override
            public void onAttachmentDataUpdated() {
                mAttachmentAdapter.triggerAttachmentDataUpdatedListener();
            }
        });

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mActivity, mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(mActivity, R.drawable.item_decoration_complete_line));

        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mListItemAdapter);
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_list_container:
                CharSequence items[] = new CharSequence[]{
                            mActivity.getResources().getString(R.string.dialog_list_attachment_options_copy),
                            mActivity.getResources().getString(R.string.dialog_list_attachment_options_delete)};

                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        //TODO: properly print items here, not json
                                        ClipboardUtil.copyToClipboard(mActivity, mCurrent.getItemsJson());
                                        break;
                                    case 1:
                                        mAttachmentAdapter.deleteAttachment(mPosition);
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
}
