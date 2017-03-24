package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderExtraAdapter;
import ve.com.abicelis.remindy.model.ReminderExtraAudio;
import ve.com.abicelis.remindy.model.ReminderExtraText;

/**
 * Created by abice on 13/3/2017.
 */

public class EditExtraAudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ReminderExtraAdapter mAdapter;
    private Activity mActivity;

    //UI
    private ImageView mRecord;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mElapsed;
    private TextView mRemaining;


    //DATA
    private ReminderExtraAudio mCurrent;
    private int mPosition;

    public EditExtraAudioViewHolder(View itemView) {
        super(itemView);

        mRecord = (ImageView) itemView.findViewById(R.id.item_extra_audio_rec);
        mPlayPause = (ImageView) itemView.findViewById(R.id.item_extra_audio_play_pause);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.item_extra_audio_seek);
        mElapsed = (TextView) itemView.findViewById(R.id.item_extra_audio_time_elapsed);
        mRemaining = (TextView) itemView.findViewById(R.id.item_extra_audio_time_remaining);
    }


    public void setData(ReminderExtraAdapter adapter, Activity activity, ReminderExtraAudio current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        //TODO: Audio stuff here
    }


    public void setListeners() {
        mRecord.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_extra_audio_rec:
                Toast.makeText(mActivity, "ReminderExtraAudio REC clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_extra_audio_play_pause:
                Toast.makeText(mActivity, "ReminderExtraAudio PLAY/PAUSE clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
