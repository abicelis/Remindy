package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;

/**
 * Created by abice on 13/3/2017.
 */

public class AudioAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mElapsed;
    private TextView mRemaining;


    //DATA
    private AudioAttachment mCurrent;
    private int mPosition;

    public AudioAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_container);
        mPlayPause = (ImageView) itemView.findViewById(R.id.item_attachment_audio_play_pause);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.item_attachment_audio_seek);
        mElapsed = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_elapsed);
        mRemaining = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_remaining);
    }


    public void setData(AttachmentAdapter adapter, Activity activity, AudioAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        //TODO: Audio stuff here
    }


    public void setListeners() {
        mContainer.setOnLongClickListener(this);
        mPlayPause.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_audio_play_pause:
                Toast.makeText(mActivity, "PLAY/PAUSE clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_audio_container:
                Toast.makeText(mActivity, "Container LONG clicked, pos= " + mPosition, Toast.LENGTH_SHORT).show();
                return true;

        }
        return false;
    }
}
