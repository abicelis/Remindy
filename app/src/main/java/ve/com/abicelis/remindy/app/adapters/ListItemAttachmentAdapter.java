package ve.com.abicelis.remindy.app.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.holders.AudioAttachmentViewHolder;
import ve.com.abicelis.remindy.app.holders.ImageAttachmentViewHolder;
import ve.com.abicelis.remindy.app.holders.LinkAttachmentViewHolder;
import ve.com.abicelis.remindy.app.holders.ListAttachmentViewHolder;
import ve.com.abicelis.remindy.app.holders.ListItemAttachmentViewHolder;
import ve.com.abicelis.remindy.app.holders.TextAttachmentViewHolder;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.model.attachment.Attachment;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;
import ve.com.abicelis.remindy.model.attachment.ImageAttachment;
import ve.com.abicelis.remindy.model.attachment.LinkAttachment;
import ve.com.abicelis.remindy.model.attachment.ListAttachment;
import ve.com.abicelis.remindy.model.attachment.ListItemAttachment;
import ve.com.abicelis.remindy.model.attachment.TextAttachment;
import ve.com.abicelis.remindy.util.FileUtil;

/**
 * Created by abice on 19/3/2017.
 */

public class ListItemAttachmentAdapter extends RecyclerView.Adapter<ListItemAttachmentViewHolder> {

    //DATA
    private List<ListItemAttachment> mListItems;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public ListItemAttachmentAdapter(Activity activity, List<ListItemAttachment> items) {
        mActivity = activity;
        mListItems = items;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public ListItemAttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListItemAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(ListItemAttachmentViewHolder holder, int position) {
        ListItemAttachment current = mListItems.get(position);

        holder.setData(this, mActivity, current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }



    public void deleteItem(int position) {      //Called from viewHolder when deleting an item
        mListItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void insertNewBlankItem() {
        mListItems.add(new ListItemAttachment());
        notifyItemInserted(getItemCount()-1);
    }
}
