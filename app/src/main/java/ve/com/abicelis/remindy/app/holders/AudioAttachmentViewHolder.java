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

public class AudioAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RecordAudioDialogFragment.RecordAudioDialogFinishListener {

    //CONST
    private static final String TAG = AudioAttachmentViewHolder.class.getSimpleName();
    private static final int REPEAT_INTERVAL_TIME = 100;


    //DATA
    private Activity mActivity;
    private AttachmentAdapter mAdapter;
    private AudioAttachment mCurrent;
    private int mPosition;

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
    private ImageView mRecord;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mElapsed;
    private TextView mRemaining;



    public AudioAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_container);
        mTapToAdd = (TextView) itemView.findViewById(R.id.item_attachment_audio_tap_to_add);
        mPlayerContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_audio_player_container);
        mRecord = (ImageView) itemView.findViewById(R.id.item_attachment_audio_record);
        mPlayPause = (ImageView) itemView.findViewById(R.id.item_attachment_audio_play_pause);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.item_attachment_audio_seek);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged. isplaying=" + mPlayer.isPlaying());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch. isplaying=" + mPlayer.isPlaying());

                if(mPlayer.isPlaying())
                    mResumePlayingFlag = true;

                pausePlaying();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch. isplaying=" + mPlayer.isPlaying());
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

    public void setData(AttachmentAdapter adapter, Activity activity, AudioAttachment current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        audioAttachmentDir = FileUtil.getAudioAttachmentDir(mActivity);
        mTimeHandler = new Handler();

        if(mCurrent.getAudioFilename() != null && !mCurrent.getAudioFilename().isEmpty()) {
            setupAudioPlayer(true);
        } else
            setupAudioPlayer(false);

        //setupAudioPlayer();
    }

    private void setupAudioPlayer(boolean hasRecording) {
        //Clear listeners
        mContainer.setOnClickListener(null);
        mPlayPause.setOnClickListener(null);
        mRecord.setOnClickListener(null);

        if(hasRecording) {
            initMediaPlayer();
            mPlayPause.setOnClickListener(this);
            mRecord.setOnClickListener(this);
            mTapToAdd.setVisibility(View.GONE);
            mPlayerContainer.setVisibility(View.VISIBLE);

            mElapsed.setText("00:00");
            mRemaining.setText(mRemainingTimeStr);
            mSeekBar.setProgress(0);

        } else {
            mContainer.setOnClickListener(this);
            mTapToAdd.setVisibility(View.VISIBLE);
            mPlayerContainer.setVisibility(View.GONE);
        }
    }
    public void setListeners() {
        //Listeners set in setupAudioPlayer()
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
        if(mPlayer == null)
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
            case R.id.item_attachment_audio_container:
                FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                RecordAudioDialogFragment recordDialog = RecordAudioDialogFragment.newInstance();
                recordDialog.setListener(this);
                recordDialog.show(fm, "RecordAudioDialogFragment");
            break;

            case R.id.item_attachment_audio_play_pause:
                if(mPlayer.isPlaying()) {
                    pausePlaying();
                } else {
                    startPlaying();
                }
                break;

            case R.id.item_attachment_audio_record:
                pausePlaying();
                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setTitle(mActivity.getResources().getString(R.string.dialog_record_audio_overwrite_title))
                        .setMessage(mActivity.getResources().getString(R.string.dialog_record_audio_overwrite_message))
                        .setPositiveButton(mActivity.getResources().getString(R.string.dialog_record_audio_overwrite_positive),  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                stopPlaying();

                                //Delete old audio
                                if(!mCurrent.getAudioFilename().isEmpty()) {
                                    File audioAttachmentDir = FileUtil.getAudioAttachmentDir(mActivity);
                                    File audioFile = new File(audioAttachmentDir, mCurrent.getAudioFilename());
                                    audioFile.delete();
                                }

                                FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                                RecordAudioDialogFragment recordAudioDialog = RecordAudioDialogFragment.newInstance();
                                recordAudioDialog.setListener(AudioAttachmentViewHolder.this);
                                recordAudioDialog.show(fm, "RecordAudioDialogFragment");
                            }
                        })
                        .setNegativeButton(mActivity.getResources().getString(R.string.dialog_record_audio_overwrite_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;

        }
    }

    @Override
    public void onFinishedRecording(String audioFileName) {
        if(!audioFileName.isEmpty()) {
            mCurrent.setAudioFilename(audioFileName);
            setupAudioPlayer(true);
        }
    }



    // updates the visualizer every 50 milliseconds
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
