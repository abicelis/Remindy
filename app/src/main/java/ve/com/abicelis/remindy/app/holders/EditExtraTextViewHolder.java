package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderExtraAdapter;
import ve.com.abicelis.remindy.model.attachment.TextAttachment;

/**
 * Created by abice on 13/3/2017.
 */

public class EditExtraTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ReminderExtraAdapter mAdapter;
    private Activity mActivity;

    //UI
    private EditText mText;

    //DATA
    private TextAttachment mCurrent;
    private int mPosition;

    public EditExtraTextViewHolder(View itemView) {
        super(itemView);

        mText = (EditText) itemView.findViewById(R.id.item_extra_text_content);
    }


    public void setData(ReminderExtraAdapter adapter, Activity activity, TextAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        mText.setText(mCurrent.getText());
    }


    public void setListeners() {
        mText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_extra_text_content:
                Toast.makeText(mActivity, "TextAttachment text clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
