package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.PlaceAdapter;
import ve.com.abicelis.remindy.model.Place;

/**
 * Created by abice on 13/3/2017.
 */

public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private PlaceAdapter mAdapter;
    private Activity mActivity;

    //UI
    private RelativeLayout mContainer;
    private TextView mAlias;
    private TextView mAddress;
    private ImageView mMap;


    //DATA
    private Place mCurrent;
    private int mPlacePosition;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_place_container);
        mAlias = (TextView) itemView.findViewById(R.id.item_place_alias);
        mAddress = (TextView) itemView.findViewById(R.id.item_place_address);
        mMap = (ImageView) itemView.findViewById(R.id.item_place_map);

    }


    public void setData(PlaceAdapter adapter, Activity activity, Place current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPlacePosition = position;

        mAlias.setText(mCurrent.getAlias());
        mAddress.setText(mCurrent.getAddress());

    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_place_container:
                Toast.makeText(mActivity, "Place'" + mCurrent.getAlias() + "' clicked! Pos=" + mPlacePosition, Toast.LENGTH_SHORT).show();
//                Pair[] pairs = new Pair[1];
//                pairs[0] = new Pair<View, String>(mImage, mFragment.getResources().getString(R.string.transition_name_expense_detail_image));
//                //pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
//                //pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
//                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));
//
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);
//                Intent expenseDetailIntent = new Intent(mFragment.getActivity(), ExpenseDetailActivity.class);
//                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_EXPENSE, mCurrent);
//                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_CREDIT_PERIOD_ID, mCreditPeriodId);
//                mFragment.startActivityForResult(expenseDetailIntent, Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());

                break;
        }
    }

}
