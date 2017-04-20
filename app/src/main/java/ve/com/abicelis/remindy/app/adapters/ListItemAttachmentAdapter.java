package ve.com.abicelis.remindy.app.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.holders.ListItemAttachmentViewHolder;
import ve.com.abicelis.remindy.model.attachment.ListItemAttachment;

/**
 * Created by abice on 19/3/2017.
 */

public class ListItemAttachmentAdapter extends RecyclerView.Adapter<ListItemAttachmentViewHolder> {

    //DATA
    private List<ListItemAttachment> mListItems;
    private Activity mActivity;
    private boolean mRealTimeDataPersistence;
    private LayoutInflater mInflater;
    private AttachmentDataUpdatedListener attachmentDataUpdatedListener;


    public ListItemAttachmentAdapter(Activity activity, List<ListItemAttachment> items, boolean realTimeDataPersistence) {
        mActivity = activity;
        mListItems = items;
        mRealTimeDataPersistence = realTimeDataPersistence;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public ListItemAttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListItemAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(ListItemAttachmentViewHolder holder, int position) {
        ListItemAttachment current = mListItems.get(position);

        holder.setData(this, mActivity, current, position, mRealTimeDataPersistence);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }



    public void deleteItem(int position) {      //Called from viewHolder when deleting an item
        mListItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount()-1);
    }

    public void insertNewBlankItem() {
        mListItems.add(new ListItemAttachment());
        notifyItemInserted(getItemCount()-1);
    }



    public void setAttachmentDataUpdatedListener(AttachmentDataUpdatedListener listener) {
        this.attachmentDataUpdatedListener = listener;
    }
    public void triggerAttachmentDataUpdatedListener() {       //Called from view holders when mRealTimeDataPersistence == true, to notify caller ListAttachmentViewHolder that the data has been updated
        if(attachmentDataUpdatedListener != null)
            attachmentDataUpdatedListener.onAttachmentDataUpdated();
    }
    public interface AttachmentDataUpdatedListener {
        void onAttachmentDataUpdated();
    }

}
