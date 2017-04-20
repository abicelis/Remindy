package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.dialogs.RecordAudioDialogFragment;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;
import ve.com.abicelis.remindy.util.FileUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class AudioAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, RecordAudioDialogFragment.RecordAudioDialogFinishListener {

    //CONST
    private static final String TAG = AudioAttachmentViewHolder.class.getSimpleName();
    private static final int REPEAT_INTERVAL_TIME = 100;
    private static final int SEEKBAR_MAX = 100;


    //DATA
    private Activity mActivity;
    private AttachmentAdapter mAdapter;
    private AudioAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    private MediaPlayer mPlayer;
    private File audioAttachmentDir;
    private boolean mResumePlayingFlag;
    private long mDurationTime = 0L;
    private Handler mTimeHandler = null;
    private String mRemainingTimeStr;


    //UI
    private LinearLayout mContainer;
    private TextView mTapToAdd;
    private LinearLayout mPlayerContainer;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mElapsed;
    private TextView mRemaining;



    public AudioAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_container);
        mTapToAdd = (TextView) itemView.findViewById(R.id.item_attachment_audio_tap_to_add);
        mPlayerContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_player_container);
        mPlayPause = (ImageView) itemView.findViewById(R.id.item_attachment_audio_play_pause);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.item_attachment_audio_seek);
        mSeekBar.setMax(SEEKBAR_MAX);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(mPlayer.isPlaying()) {
                    mResumePlayingFlag = true;
                    pausePlaying();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.start();
                mPlayer.pause();

                int seekTo = (int) ((double) mSeekBar.getProgress() * (1 / (double) mSeekBar.getMax()) * (double) mPlayer.getDuration());
                seekPlayerTo(seekTo);
                if(mResumePlayingFlag) {
                    mResumePlayingFlag = false;
                    startPlaying();
                }
            }
        });

        mElapsed = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_elapsed);
        mRemaining = (TextView) itemView.findViewById(R.id.item_attachment_audio_time_remaining);

    }

    public void setData(AttachmentAdapter adapter, Activity activity, AudioAttachment current, int position, boolean realTimeDataPersistence) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;
        audioAttachmentDir = FileUtil.getAudioAttachmentDir(mActivity);
        mTimeHandler = new Handler();

        mContainer.setOnLongClickListener(this);
        mPlayPause.setOnClickListener(this);

        setupViewHolder();

        if(mCurrent.getAudioFilename() == null || mCurrent.getAudioFilename().isEmpty())
            showRecordAudioDialogFragment();
    }

    private void setupViewHolder() {
        mPlayerContainer.setVisibility(View.GONE);
        mTapToAdd.setVisibility(View.GONE);

        if(mCurrent.getAudioFilename() == null || mCurrent.getAudioFilename().isEmpty()) {
            mTapToAdd.setVisibility(View.VISIBLE);
            mPlayerContainer.setVisibility(View.GONE);

        } else {
            initMediaPlayer();
            mTapToAdd.setVisibility(View.GONE);
            mPlayerContainer.setVisibility(View.VISIBLE);

            mElapsed.setText("00:00");
            mRemaining.setText(mRemainingTimeStr);
            mSeekBar.setProgress(0);
        }
    }

    public void setListeners() {
        //Listeners set in setData()
    }


    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        try {

            String audioFilePath = new File(audioAttachmentDir, "/" + mCurrent.getAudioFilename()).getAbsolutePath();
            mPlayer.setDataSource(audioFilePath);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayPause.setImageResource(R.drawable.icon_play);
                    mSeekBar.setProgress(0);
                    mElapsed.setText("00:00");
                    mRemaining.setText(mRemainingTimeStr);
                }
            });

            //Get audio duration
            mDurationTime = mPlayer.getDuration();

            //Set remaining time string
            int remainingSecs = (int) (mDurationTime / 1000);
            int remainingMins = remainingSecs / 60;
            remainingSecs = remainingSecs % 60;
            mRemainingTimeStr = String.format(Locale.getDefault(), "%02d", remainingMins) + ":" + String.format(Locale.getDefault(), "%02d", remainingSecs);

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void seekPlayerTo(int seekTo) {
        if(mPlayer != null)
            mPlayer.release();
        initMediaPlayer();
        mPlayer.seekTo(seekTo);
    }

    private void startPlaying() {
        if(mPlayer == null)
            initMediaPlayer();

        mPlayPause.setImageResource(R.drawable.icon_pause);
        mPlayer.start();
        mTimeHandler.postDelayed(updateTime, 0);
    }


    private void pausePlaying() {
        if(mPlayer != null)
            mPlayer.pause();
        mPlayPause.setImageResource(R.drawable.icon_play);
    }

    private void stopPlaying() {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_audio_play_pause:
                if(mPlayer.isPlaying()) {
                    pausePlaying();
                } else {
                    startPlaying();
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_audio_container:
                pausePlaying();
                CharSequence items[] = new CharSequence[] {
                        mActivity.getResources().getString(R.string.dialog_audio_attachment_options_replace),
                        mActivity.getResources().getString(R.string.dialog_audio_attachment_options_delete)};


                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    stopPlaying();

                                    //Delete old audio
                                    if(!mCurrent.getAudioFilename().isEmpty()) {
                                        File audioAttachmentDir = FileUtil.getAudioAttachmentDir(mActivity);
                                        File audioFile = new File(audioAttachmentDir, mCurrent.getAudioFilename());
                                        audioFile.delete();
                                    }
                                    showRecordAudioDialogFragment();
                                }else if(which == 1) {
                                    mAdapter.deleteAttachment(mPosition);
                                }


                            }
                        })
                        .create();
                dialog.show();
                return true;
        }

        return false;
    }

    private void showRecordAudioDialogFragment() {
        FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
        RecordAudioDialogFragment recordDialog = RecordAudioDialogFragment.newInstance(mCurrent);
        recordDialog.setListener(this);
        recordDialog.show(fm, "RecordAudioDialogFragment");
    }

    @Override
    public void onFinishedRecording(AudioAttachment audioAttachment) {
        if(!audioAttachment.getAudioFilename().isEmpty()) {
            mCurrent.setAudioFilename(audioAttachment.getAudioFilename());
            setupViewHolder();
            mAdapter.triggerShowAttachmentHintListener();
        }
    }



    // updates mElapsed mRemaining and mSeekBar
    Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null && mPlayer.isPlaying()) // if we are playing
            {
                //long elapsedMillis = SystemClock.uptimeMillis() - mStartTime;
                long elapsedMillis = mPlayer.getCurrentPosition();

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
