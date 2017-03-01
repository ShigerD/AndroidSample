package com.clw.video;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

public class VideoApplication extends Application {

    private static final String TAG = VideoApplication.class.getSimpleName();
    private IAudioFocusListenter mMyAudioFocusListener;
    private AudioManager mAudioManager;
    private boolean mHasAbandon = true;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }
    
    public boolean requestAudioFocus(boolean oneshot) {
        boolean success = false;
        if (oneshot) {
            success = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mAudioFocusListener, 
                    AudioManager.STREAM_CAR_VIDEO, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        } else {
            success = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mAudioFocusListener, 
                    AudioManager.STREAM_CAR_VIDEO, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (success) {
            mHasAbandon = false;
        }
        return success;
    }
    
    public void abandonAudioFocus() {
        mHasAbandon = true;
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }
    
    public boolean hasAbandon() {
        return mHasAbandon;
    }
    
    public void registerMyAudioFocusListener(IAudioFocusListenter listener) {
        this.mMyAudioFocusListener = listener;
    }
    
    public OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "=>onAudioFocusChange= focusChange = " + focusChange);
            if (mMyAudioFocusListener != null) {
                mMyAudioFocusListener.onAudioFocusChange(focusChange);
            }
        }
    };
    
    public interface IAudioFocusListenter {
        public void onAudioFocusChange(int focusChange);
    }

}
