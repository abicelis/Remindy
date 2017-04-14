package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.transitionseverywhere.TransitionManager;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.RecordAudioDialogFragment;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;
import ve.com.abicelis.remindy.util.ConversionUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class AudioAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RecordAudioDialogFragment.RecordAudioDialogFinishListener {

    //CONST
    private static final String TAG = AudioAttachmentViewHolder.class.getSimpleName();
    private static final int REPEAT_INTERVAL_TIME = 100;

    //DATA
    private AttachmentAdapter mAdapter;
    private Activity mActivity;
    private boolean hasRecording = false;
    private MediaPlayer mPlayer;
    private File audioAttachmentDir;
    private boolean isPlaying;
    private long mStartTime = 0L;
    private long mDurationTime = 0L;
    private AudioAttachment mCurrent;
    private int mPosition;

    //UI
    private LinearLayout mContainer;
    private TextView mTapToAdd;

    private LinearLayout mPlayerContainer;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mElapsed;
    private TextView mRemaining;
    private Handler mTimeHandler = null;



    public AudioAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_container);
        mTapToAdd = (TextView) itemView.findViewById(R.id.item_attachment_audio_tap_to_add);
        mPlayerContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_player_container);
        mPlayPause = (ImageView) itemView.findViewById(R.id.item_attachment_audio_play_pause);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.item_attachment_audio_seek);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(isPlaying) {
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(isPlaying) {
                    mPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isPlaying) {
                    int seekTo = (int) ((double) mSeekBar.getProgress() * (1 / (double) mSeekBar.getMax()) * (double) mPlayer.getDuration());
                    mPlayer.seekTo(seekTo);
                    mPlayer.start();
                }

            }
        });

        mElapsed = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_elapsed);
        mRemaining = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_remaining);

    }


    public void setData(AttachmentAdapter adapter, Activity activity, AudioAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        if(mCurrent.getAudioFilename() != null && !mCurrent.getAudioFilename().isEmpty())
            hasRecording = true;
        else
            hasRecording = false;

        audioAttachmentDir = new File(mActivity.getExternalFilesDir(null), mActivity.getResources().getString(R.string.subdirectory_attachments_audio));
        setupAudioPlayer();
        mTimeHandler = new Handler();
    }

    private void setupAudioPlayer() {
        if(hasRecording) {
            mTapToAdd.setVisibility(View.GONE);
            mPlayerContainer.setVisibility(View.VISIBLE);
        } else {
            mTapToAdd.setVisibility(View.VISIBLE);
            mPlayerContainer.setVisibility(View.GONE);
        }

    }


    public void setListeners() {
        mContainer.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {

            String audioFilePath = new File(audioAttachmentDir, "/" + mCurrent.getAudioFilename()).getAbsolutePath();
            mPlayer.setDataSource(audioFilePath);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    mRemaining.setText("00:00");
                    mElapsed.setText("00:00");
                }
            });

            //Reset time
            mDurationTime = mPlayer.getDuration();
            mStartTime = SystemClock.uptimeMillis();
            mTimeHandler.postDelayed(updateTime, 0);

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_audio_play_pause:
                if(hasRecording && !isPlaying) {
                    isPlaying = true;
                    startPlaying();
                }
                break;

            case R.id.item_attachment_audio_container:
                if(!hasRecording) {
                    FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                    RecordAudioDialogFragment dialog = RecordAudioDialogFragment.newInstance();
                    dialog.setListener(this);
                    dialog.show(fm, "RecordAudioDialogFragment");
                }

        }
    }

    @Override
    public void onFinishedRecording(String audioFileName) {
        if(!audioFileName.isEmpty()) {
            mCurrent.setAudioFilename(audioFileName);
            hasRecording = true;
            setupAudioPlayer();
        }
    }



    // updates the visualizer every 50 milliseconds
    Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) // if we are playing
            {
                long elapsedMillis = SystemClock.uptimeMillis() - mStartTime;

                int progress = (int) (((float)elapsedMillis / mDurationTime) * 100);
                int elapsedSecs = (int) (elapsedMillis / 1000);
                int remainingSecs = (int) (mDurationTime / 1000) - elapsedSecs;

                int elapsedMins = elapsedSecs / 60;
                elapsedSecs = elapsedSecs % 60;

                int remainingMins = remainingSecs / 60;
                remainingSecs = remainingSecs % 60;


                mElapsed.setText(String.format(Locale.getDefault(), "%02d", elapsedMins) + ":" + String.format(Locale.getDefault(), "%02d", elapsedSecs));
                mRemaining.setText(String.format(Locale.getDefault(), "%02d", remainingMins) + ":" + String.format(Locale.getDefault(), "%02d", remainingSecs));
                mSeekBar.setProgress(progress);

                mTimeHandler.postDelayed(this, REPEAT_INTERVAL_TIME);
            }
        }
    };


}
