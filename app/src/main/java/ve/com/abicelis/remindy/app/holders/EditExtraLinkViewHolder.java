package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderExtraAdapter;
import ve.com.abicelis.remindy.model.ReminderExtraLink;

/**
 * Created by abice on 13/3/2017.
 */

public class EditExtraLinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ReminderExtraAdapter mAdapter;
    private Activity mActivity;

    //UI
    private EditText mLink;

    //DATA
    private ReminderExtraLink mCurrent;
    private int mPosition;

    public EditExtraLinkViewHolder(View itemView) {
        super(itemView);

        mLink = (EditText) itemView.findViewById(R.id.item_extra_link_content);
    }


    public void setData(ReminderExtraAdapter adapter, Activity activity, ReminderExtraLink current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        mLink.setText(mCurrent.getLink());
    }


    public void setListeners() {
        mLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_extra_link_content:
                Toast.makeText(mActivity, "ReminderExtraLink link clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
