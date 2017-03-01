
package com.clw.video;

import java.io.File;
import java.util.ArrayList;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.mcu.McuManager;
import android.mcu.Setup;

import com.clw.vehicle.VehicleSignalObserver;
import com.clw.vehicle.VehicleManager;

public class MediaPlaybackFragment extends Fragment implements OnClickListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, View.OnTouchListener {

    private final String TAG = MediaPlaybackFragment.class.getSimpleName();
    
    private static final int MSG_UPDATE_PROGRESS = 0x01;
    private static final int MSG_NOTIFY_POSITION = 0x02;
    private static final int MSG_FADE_OUT        = 0x03;
    private static final int MSG_BRAKE_ON        = 0x04;
    private static final int MSG_BRAKE_OFF       = 0x05;
    private static final int MSG_NOTIFY_MEDIA_INFO = 0x06;
    private static final int MSG_UPDATE_PLAY_STATE = 0x07;

    public static final int DEFAULT_TIMEOUT            = 5000;
    private static final int DEFUALT_DURATION_RETREAT  = 3000; 
    
    private CustomVideoView mVideoView;
    private SeekBar mSeekBar;
    private TextView mCurrentTime ;
    private TextView mTotalTime;
    private ImageView mRatio;
    private ImageView mPlayPause;
    private View mMediaControlBar, mBrakeView, mLoadingView;
    
    private VideoTabActivity mActivity;
    private VideoApplication mApplication;
    private StoreManager mStoreManager;
    private VehicleSignalObserver mVehicle;
    private VehicleManager mVehicleManager;
    
    private ArrayList<Uri> mPlayList;
    private int mPositionToPlay;
    // duration position!
    private int mDurationPosition = -1;
    private boolean mIsPlayingMode;
    private Uri mUri;
    private Store mStore;
    private MediaPlaylistFragment.OnPositionChangedListener mPositionListener;

    private boolean mDragging = false;
    
    private Animation mShowControlBarAnimation;
    private Animation mHideControlBarAnimation;
    private boolean mShowing = true;
    
    private boolean mTransientLossOfFocus;
    private boolean mNeedResumePlay;
    private int mCurrentPage = 1;
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=onCreate=");
        mActivity = (VideoTabActivity)getActivity();
        mVehicleManager = VehicleManager.getInstance();
        mApplication = (VideoApplication)mActivity.getApplication();
        mApplication.registerMyAudioFocusListener(mMyAudioFocusListener);
        mStoreManager = StoreManager.getInstance(mActivity);
        mVehicle = VehicleSignalObserver.getInstance();
        mVehicle.register(mVehicleListener);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "=onCreateView=");
        View view = inflater.inflate(R.layout.video_playback_layout, container, false);
        setupView(view);
        return view ;
    }
    
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=onResume=");
        if (isBrakeOn()) {
            mHandler.sendEmptyMessage(MSG_BRAKE_ON);
        } else {
            mHandler.sendEmptyMessage(MSG_BRAKE_OFF);
        }
        // don't play while audio focus transient loss.
        if (!mTransientLossOfFocus && !isMediaPlaying()) {
            if (isPlayingMode()) {
                mVideoView.start();
            } else {
                play(mUri);
            }
        }
        mHandler.removeMessages(MSG_NOTIFY_MEDIA_INFO);
        mHandler.sendEmptyMessage(MSG_NOTIFY_MEDIA_INFO);
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
    }
    
    public void onPause() {
        super.onPause();
        Log.d(TAG, "=onPause=");
        pause();
        mHandler.removeCallbacksAndMessages(null);
    }
    
    public void onStop() {
        super.onStop();
        Log.d(TAG, "=onStop=");
        stop();
    }
    
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=onDestroy=");
        mVideoView.setVisibility(View.GONE);
        mVehicle.unregister(mVehicleListener);
        if (!mApplication.hasAbandon()) {
            mApplication.abandonAudioFocus();
        }
    }
    
    private void setupView(View view){
        mMediaControlBar = view.findViewById(R.id.media_controller_bar);
        mBrakeView = view.findViewById(R.id.disclaimer);
        mBrakeView.findViewById(R.id.disclaimer_message).setOnClickListener(mBrakeBtnListener);
        mVideoView = (CustomVideoView)view.findViewById(R.id.video_surface_view);
        mSeekBar = (SeekBar)view.findViewById(R.id.seekBar);
        mLoadingView = view.findViewById(R.id.video_loading);
        mCurrentTime = (TextView)view.findViewById(R.id.tv_startTime);
        mTotalTime = (TextView)view.findViewById(R.id.tv_totalTime);
        view.findViewById(R.id.img_previous).setOnClickListener(this);
        view.findViewById(R.id.img_next).setOnClickListener(this);
        view.findViewById(R.id.img_dir).setOnClickListener(this);
        view.findViewById(R.id.img_stop).setOnClickListener(this);
        mRatio = (ImageView)view.findViewById(R.id.img_full);
        mPlayPause = (ImageView)view.findViewById(R.id.img_play);
        mRatio.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChanged);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnTouchListener(this);
        boolean fullMode = mStoreManager.getBoolean(StoreManager.KEY_SCREEN_MODE, false);
        mVideoView.setScreenFull(fullMode);
        updateFullBtnState(fullMode);
    }
    
    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        if (arg1.getAction() == MotionEvent.ACTION_UP) {
            if (mCurrentPage == 2) {
                mActivity.switchToPage(1);
                return true;
            }
            if (mShowing) {
                hideBars();
            } else {
                showBars(DEFAULT_TIMEOUT);
            }
        }
        return true;
    }
    
    @Override
    public void onClick(View view) {
        showBars(DEFAULT_TIMEOUT);
        switch(view.getId()){
            case R.id.img_full:
                boolean fullMode = mStoreManager.getBoolean(StoreManager.KEY_SCREEN_MODE, false);
                if (fullMode) {
                    mRatio.setBackgroundResource(R.drawable.mc_full);
                } else {
                    mRatio.setBackgroundResource(R.drawable.mc_notfull);
                }
                fullMode = !fullMode;
                mVideoView.setScreenFull(fullMode);
                mStoreManager.putBoolean(StoreManager.KEY_SCREEN_MODE, fullMode);
                break;
            case R.id.img_stop:
                stop();
                break;
            case R.id.img_previous:
                playPrevious();
                break;
            case R.id.img_play:
                if (!isPlayingMode()) {
                    doPlay(0);
                } else if(mVideoView.isPlaying()) {
                    pause();
                } else {
                    resume();
                }
                break ;
            case R.id.img_next:
                playNext();
                break ;
            case R.id.img_dir:
                mActivity.switchToPage(0);
                break ;
           default :
               break ;
        }
    }
    
    public void listPlay(Store store, ArrayList<Uri> playList) {
        this.mStore = store;
        this.mPlayList = playList;
        if (isSamePlaying(mUri)) {
            mPositionToPlay = mPlayList.indexOf(mUri);
            mHandler.sendEmptyMessage(MSG_NOTIFY_POSITION);
            if (!isPlayingMode()) {
                play(mUri);
            }
            return;
        } 
        // default play first one.
        play(0);
    }
    
    public void clearPlayList() {
        mPlayList.clear();
        mUri = null;
    }

    public void play(Uri uri) {
        mUri = uri;
        if (uri != null && mStoreManager.getString(StoreManager.KEY_CUR_URI, "").equals(uri.toString())) {
            // play from store duration postion.
            mDurationPosition = mStoreManager.getInt(StoreManager.KEY_DUR_POSITION, 0);
            mDurationPosition -= DEFUALT_DURATION_RETREAT;
        } else if (uri != null) {
            mDurationPosition = 0;
            mStoreManager.putInt(StoreManager.KEY_DUR_POSITION, 0);
        }
        doPlay(mDurationPosition);
    }
    
    public void play(int index) {
        if (mPlayList == null || mPlayList.size() == 0) {
            Log.d(TAG, "Nothing to play. mPlayList.size = 0!");
            return;
        }
        mPositionToPlay = index;
        mHandler.sendEmptyMessage(MSG_NOTIFY_POSITION);
        mUri = mPlayList.get(index);
        // play from start.
        doPlay(0);
    }
    
    public boolean isSamePlaying(Uri uri) {
        if (mPlayList.contains(uri)) {
            // dothing, still play same one
            return true;
        }
        return false;
    }
    
    public boolean isPlayingMode() {
        return mIsPlayingMode;
    }
    
    public boolean isMediaPlaying() {
        if (isPlayingMode()) {
            return mVideoView.isPlaying();
        } else {
            return false;
        }
    }
    
    public void resume() {
        if (!canPlay()) {
            return;
        }
        if (isPlayingMode()) {
            mVideoView.start();
        } else {
            play(mUri);
        }
        updatePlayBtnState();
    }
    
    public void pause() {
        mNeedResumePlay = true;
        mDurationPosition = mVideoView.getCurrentPosition();
        Log.d(TAG, "=pause= mDurationPosition = " + mDurationPosition);
        mStoreManager.putInt(StoreManager.KEY_DUR_POSITION, mDurationPosition);
        mVideoView.pause();
        updatePlayBtnState();
    }
    
    public void stop() {
        mIsPlayingMode = false;
        mVideoView.stopPlayback();
        updatePlayBtnState();
    }
    
    public void playNext() {
        if (mPlayList == null || mPlayList.size() == 0) {
            return;
        }
        mPositionToPlay = (++mPositionToPlay) % mPlayList.size();
        if (mVideoView.isPlaying()) {
            VideoUtils.autoMute();
            mVideoView.stopPlayback();
        }
        play(mPositionToPlay);
    }
    
    public void playPrevious() {
        if (mPlayList == null || mPlayList.size() == 0) {
            return;
        }
        mPositionToPlay = (--mPositionToPlay) % mPlayList.size();
        if (mPositionToPlay < 0) {
            mPositionToPlay = mPlayList.size() - 1;
        }
        if (mVideoView.isPlaying()) {
            VideoUtils.autoMute();
            mVideoView.stopPlayback();
        }
        play(mPositionToPlay);
    }
    
    public Store getPlayingStore() {
        return mStore;
    }
    
    public Uri getPlayingUri() {
        return mUri;
    }
    
    public void registerPositionListener(MediaPlaylistFragment.OnPositionChangedListener listener) {
        this.mPositionListener = listener;
    }
    
    public void showBars(int timeout) {
        Message msg = mHandler.obtainMessage(MSG_FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(MSG_FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
        
        if (!mShowing) {
            mShowing = true;
            mMediaControlBar.setVisibility(View.VISIBLE);
            mActivity.quitFullScreen();

            if (mShowControlBarAnimation == null) {
                mShowControlBarAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.media_control_bar_up);
            }

            mMediaControlBar.startAnimation(mShowControlBarAnimation);
        }
    }
    
    public void hideBars() {
        if (mShowing) {
            mShowing = false;
            if (mHideControlBarAnimation == null) {
                mHideControlBarAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.media_control_bar_down);
                mHideControlBarAnimation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        if (!mShowing) {
                            mMediaControlBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            mMediaControlBar.startAnimation(mHideControlBarAnimation);
            if (isBrakeOn()) {
                mActivity.enterFullScreen();
            }
        }
    }

    public void setCurrentPage(int currentPage) {
        this.mCurrentPage = currentPage;
    }
    
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "=onPrepared");
        showBars(DEFAULT_TIMEOUT);
        int maxValue = mediaPlayer.getDuration() / 1000;
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.GONE);
        }
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_STATE, 500);
        if(maxValue > 0) {
            // reset progress bar;
            mSeekBar.setProgress(0);
            mSeekBar.setMax(maxValue);
            mTotalTime.setText(VideoUtils.makeTimeString(getActivity(), maxValue));
        }
    }
    
    @Override
    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        Log.d(TAG, "=onError");
        if( arg1 == MediaPlayer.MEDIA_ERROR_UNKNOWN &&
                (arg2 == MediaPlayer.MEDIA_ERROR_IO || arg2 == 0)){
            Log.d(TAG, " error storage eject!");
            return true;
        }
        // TODO add selection for user and auto play next
        showDialog(DEFAULT_TIMEOUT / 1000);
        return true;
    }
    
    public void showDialog(int time){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.VideoView_error_title);
        builder.setMessage(R.string.videoview_error_text_unknown);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                stop();
                mActivity.switchToPage(0);
                mTimer.cancel();
            }
        });
        builder.setPositiveButton(getString(R.string.video_error_auto_next, time),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTimer.cancel();
                playNext();
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
        mTimer.start();
    }

    public CountDownTimer mTimer = new CountDownTimer(DEFAULT_TIMEOUT + 1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) (millisUntilFinished / 1000);
            if (mAlertDialog != null) {
                mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                        getString(R.string.video_error_auto_next, time));
            }
        }

        @Override
        public void onFinish() {
            playNext();
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "=onCompletion");
        playNext();
    }
    
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "=onInfo");
        updatePlayBtnState();
        return true;
    }
    
    private boolean canPlay() {
        return mApplication.requestAudioFocus(false);
    }
    
    private void doPlay(int duration) {
        Log.d(TAG, "doPlay  mUri = " + mUri);
        Uri uri = mUri;
        if (uri == null) {
            Log.d(TAG, "Nothing to play. mUri = null!");
            return;
        }
        mNeedResumePlay = true;
        if (!canPlay()) {
            return;
        }
        mVideoView.stopPlayback();
        mVideoView.setVideoURI(uri);
        if (duration > 0) {
            mVideoView.seekTo(duration);
        }
        mIsPlayingMode = true;
        mVideoView.start();
        mStoreManager.putString(StoreManager.KEY_CUR_URI, uri.toString());
    }
    
    private void notifyPosition() {
        if (mPositionListener != null) {
            mPositionListener.onPositionChanged(mPositionToPlay);
        }
    }
    
    private int setProgress(){
        if (mVideoView == null || mDragging) {
            return 0;
        }
        int position = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        if (mSeekBar != null){
            if (duration >= 0) {
                long pos = position/1000 ;
                mSeekBar.setProgress((int)pos);
            } else {
                mSeekBar.setProgress(0);
            }
        }
        
        if (mCurrentTime != null) {
            mCurrentTime.setText(VideoUtils.makeTimeString(getActivity(), position/1000));
        }
        
        return position;
    }
    
    private void updatePlayBtnState() {
        if(isMediaPlaying()) {
            mPlayPause.setBackgroundResource(R.drawable.mc_play);
        } else {
            mPlayPause.setBackgroundResource(R.drawable.mc_pause);
        }
    }
    
    private void updateFullBtnState(boolean fullMode) {
        if(fullMode) {
            mRatio.setBackgroundResource(R.drawable.mc_notfull);
        } else {
            mRatio.setBackgroundResource(R.drawable.mc_full);
        }
    }
    
    private boolean isBrakeOn() {
        if (!isBrakeDetectOn()) {
            return true;
        }
        boolean brakeOn = mVehicle.isParkingBrakeOn();
        return brakeOn;
    }
    
    private boolean isBrakeDetectOn() {
        McuManager mcu = McuManager.getInstance();
        Setup setup = mcu.getSetupInstance();
        int[] values = setup.getMcuConfiguration(Setup.SYSTEM_SETUP, Setup.SYSTEM_HANDBRAKEDETECT_ON);
        boolean detectOn;
        if (values != null && values.length > 0) {
            detectOn = values[0] == 1 ? true : false;
        } else {
            detectOn = false;
        }
        return detectOn;
    }
    
    private void notifyMediaInfo() {
        if (mPlayList != null && mUri != null) {
            File file = new File(mUri.getPath());
            String track = file.getName();
            int position = mVideoView.getCurrentPosition() / 1000;
            int duration = mVideoView.getDuration() / 1000;
            mVehicleManager.setMediaPlayInfo(mPositionToPlay, mPlayList.size(), position, duration);
            mVehicleManager.setMediaInfo(track, "", "");
        }
    }
    
    private OnSeekBarChangeListener mSeekBarChanged = new OnSeekBarChangeListener() {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            showBars(DEFAULT_TIMEOUT);
            if(mVideoView.getDuration() <= 0){
                seekBar.setProgress(0);
                return;
            }
            mDragging = false;
            mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showBars(DEFAULT_TIMEOUT);
            if(mVideoView.getDuration() <= 0){
                return;
            }
            mDragging = true;
            mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        }
        
        @Override
        public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromuser) {
            if(mVideoView.getDuration() <= 0){
                seekBar.setProgress(0);
                return;
            }
            if(!fromuser){
                return;
            }
            showBars(DEFAULT_TIMEOUT);
            long newpostion = seekBar.getProgress();
            mVideoView.seekTo((int)newpostion*1000);
            mCurrentTime.setText(VideoUtils.makeTimeString(getActivity(), newpostion));
            Log.i(TAG, "newposition = "+newpostion);
        }
    };
    
    private VideoApplication.IAudioFocusListenter mMyAudioFocusListener = new VideoApplication.IAudioFocusListenter() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    mActivity.finish();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    mTransientLossOfFocus = true;
                    if (isMediaPlaying()) {
                        mNeedResumePlay = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(TAG, "AUDIOFOCUS_GAIN");
                    mTransientLossOfFocus = false;
                    if (mNeedResumePlay) {
                        resume();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_GRANTED:
                    Log.d(TAG, "AUDIOFOCUS_GAIN_GRANTED");
                    play(mUri);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int pos;
            switch(msg.what){
                case MSG_UPDATE_PROGRESS:
                    pos = setProgress();
                    msg = obtainMessage(MSG_UPDATE_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
                case MSG_NOTIFY_POSITION:
                    notifyPosition();
                    break;
                case MSG_BRAKE_ON:
                    mBrakeView.setVisibility(View.GONE);
                    showBars(DEFAULT_TIMEOUT);
                    break;
                case MSG_BRAKE_OFF:
                    mBrakeView.setVisibility(View.VISIBLE);
                    mActivity.quitFullScreen();
                    break;
                case MSG_FADE_OUT:
                    if (mCurrentPage == 1) {
                        hideBars();
                    }
                    break;
                case MSG_NOTIFY_MEDIA_INFO:
                    if (mVideoView.isPlaying()) {
                        notifyMediaInfo();
                    }
                    sendEmptyMessageDelayed(MSG_NOTIFY_MEDIA_INFO, 1000);
                    break;
                case MSG_UPDATE_PLAY_STATE:
                    updatePlayBtnState();
                    break;
                default:
                    break;
            }
        };
    };
   
    private VehicleSignalObserver.Listener mVehicleListener = new VehicleSignalObserver.Listener() {
        public void onVehicleSignal(int signal, boolean state) {
            switch (signal) {
                case VehicleSignalObserver.SIGNAL_PARKING_BRAKE:
                    if (!isBrakeDetectOn()) {
                        break;
                    }
                    if (state) {
                        mHandler.sendEmptyMessage(MSG_BRAKE_ON);
                    } else {
                        mHandler.sendEmptyMessage(MSG_BRAKE_OFF);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener mBrakeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(Settings.ACTION_DEVICE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
