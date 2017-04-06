package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.TextViewWithCircularIndicator;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.ListAttachment;
import ve.com.abicelis.remindy.model.attachment.TextAttachment;

/**
 * Created by abice on 13/3/2017.
 */

public class ListAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private TextView mList;

    //DATA
    private ListAttachment mCurrent;
    private int mPosition;

    public ListAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_list_container);
        mList = (TextView) itemView.findViewById(R.id.item_attachment_list_content);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, ListAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        mList.setText(mCurrent.getText());
    }


    public void setListeners() {
        mContainer.setOnLongClickListener(this);
    }


    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_list_container:
                Toast.makeText(mActivity, "Container LONG clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
