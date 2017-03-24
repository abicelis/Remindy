package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderExtraAdapter;
import ve.com.abicelis.remindy.model.ReminderExtraImage;
import ve.com.abicelis.remindy.model.ReminderExtraText;

/**
 * Created by abice on 13/3/2017.
 */

public class EditExtraImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ReminderExtraAdapter mAdapter;
    private Activity mActivity;

    //UI
    private ImageView mImage;
    private LinearLayout mTapToAddContainer;

    //DATA
    private ReminderExtraImage mCurrent;
    private int mPosition;

    public EditExtraImageViewHolder(View itemView) {
        super(itemView);

        mImage = (ImageView) itemView.findViewById(R.id.item_extra_image_content);
        mTapToAddContainer = (LinearLayout) itemView.findViewById(R.id.item_extra_image_tap_to_add_container);
    }


    public void setData(ReminderExtraAdapter adapter, Activity activity, ReminderExtraImage current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;


        if(mCurrent.getThumbnail().length != 0) {
            mTapToAddContainer.setVisibility(View.GONE);
            //TODO: byte array to image bitmap
            //mImage.setImageBitmap(mCurrent.getThumbnail());
        } else {
        }
    }


    public void setListeners() {
        mTapToAddContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_extra_image_tap_to_add_container:
                Toast.makeText(mActivity, "ReminderExtraImage tap to add clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
