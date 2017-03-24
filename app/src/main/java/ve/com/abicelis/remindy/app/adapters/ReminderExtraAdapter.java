package ve.com.abicelis.remindy.app.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.holders.EditExtraAudioViewHolder;
import ve.com.abicelis.remindy.app.holders.EditExtraImageViewHolder;
import ve.com.abicelis.remindy.app.holders.EditExtraLinkViewHolder;
import ve.com.abicelis.remindy.app.holders.EditExtraTextViewHolder;
import ve.com.abicelis.remindy.enums.ReminderExtraType;
import ve.com.abicelis.remindy.model.ReminderExtra;
import ve.com.abicelis.remindy.model.ReminderExtraAudio;
import ve.com.abicelis.remindy.model.ReminderExtraImage;
import ve.com.abicelis.remindy.model.ReminderExtraLink;
import ve.com.abicelis.remindy.model.ReminderExtraText;

/**
 * Created by abice on 19/3/2017.
 */

public class ReminderExtraAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //DATA
    private List<ReminderExtra> mExtras;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public ReminderExtraAdapter(Activity activity, List<ReminderExtra> extras) {
        mActivity = activity;
        mExtras = extras;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ReminderExtraType extraType = ReminderExtraType.values()[viewType];
        switch (extraType) {
            case TEXT:
                return new EditExtraTextViewHolder(mInflater.inflate(R.layout.list_item_edit_extra_text, parent, false));
            case LINK:
                return new EditExtraLinkViewHolder(mInflater.inflate(R.layout.list_item_edit_extra_link, parent, false));
            case AUDIO:
                return new EditExtraAudioViewHolder(mInflater.inflate(R.layout.list_item_edit_extra_audio, parent, false));
            case IMAGE:
                return new EditExtraImageViewHolder(mInflater.inflate(R.layout.list_item_edit_extra_image, parent, false));
            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in ReminderExtraAdapter");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mExtras.get(position).getType().ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReminderExtra current = mExtras.get(position);

        switch (current.getType()) {
            case TEXT:
                EditExtraTextViewHolder tvh = (EditExtraTextViewHolder) holder;
                tvh.setData(this, mActivity, (ReminderExtraText)current, position);
                tvh.setListeners();
                break;

            case LINK:
                EditExtraLinkViewHolder lvh = (EditExtraLinkViewHolder) holder;
                lvh.setData(this, mActivity, (ReminderExtraLink)current, position);
                lvh.setListeners();
                break;

            case AUDIO:
                EditExtraAudioViewHolder avh = (EditExtraAudioViewHolder) holder;
                avh.setData(this, mActivity, (ReminderExtraAudio) current, position);
                avh.setListeners();
                break;

            case IMAGE:
                EditExtraImageViewHolder ivh = (EditExtraImageViewHolder) holder;
                ivh.setData(this, mActivity, (ReminderExtraImage) current, position);
                ivh.setListeners();
                break;

            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in ReminderExtraAdapter");
        }
    }


    @Override
    public int getItemCount() {
        return mExtras.size();
    }
}
