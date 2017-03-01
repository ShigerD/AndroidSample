
package com.clw.video;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import com.autochips.settings.AtcSettings.VCP;

public class VideoTabActivity extends FragmentActivity {

    private static final String TAG = VideoTabActivity.class.getSimpleName();
    
    private static final int MSG_AUTO_PLAY = 0x01;
    
    private StoreManager mStoreManager;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private boolean mAutoPlay = false;
    private boolean isTypeOnUSB = false;
    
    private MediaStorageFragment  mMediaStorageFragment  = new MediaStorageFragment();
    private MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
    private MediaPlaylistFragment mMediaPlaylistFragment = new MediaPlaylistFragment();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=onCreate=");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.video_main);
        mStoreManager = StoreManager.getInstance(this);
        mMediaStorageFragment.setMediaPlaybackFragment(mMediaPlaybackFragment);
        mMediaStorageFragment.setMediaPlaylistFragment(mMediaPlaylistFragment);
        mMediaPlaylistFragment.registerReadyListener(mReadyListener);
        initViewPager();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "=onNewIntent= intent.getData() = " + intent.getData());
        
        Uri uri = intent.getData();
        if (uri != null) {
            // play from filemanager
            play(uri);
            MediaModel.getInstance().startFolderVideoLoader(uri, mFolderLoadListener);
        }
    }
    
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=onResume=");
        ScreenUtils.getInstance().init();
        if (ScreenUtils.getInstance().mDefalutValues != null) {
        	VCP.SetVcpAppOn(VCP.SrcType.USB);
        	isTypeOnUSB = true;
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (isTypeOnUSB) {
    		VCP.SetVcpOffReg();
    		isTypeOnUSB = false;
    	}
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	Log.d(TAG, "---onWindowFocusChanged");
    	if (hasFocus) {
    		if (ScreenUtils.getInstance().mDefalutValues != null) {
    			ScreenUtils.getInstance().setDefaultScreen();
    			ScreenUtils.getInstance().setDefaultYUV();
    		}
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MCU_NEXT:
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MCU_PREV:
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MCU_PLAY:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MCU_NEXT:
                mMediaPlaybackFragment.playNext();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MCU_PREV:
                mMediaPlaybackFragment.playPrevious();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MCU_PLAY:
                if (mMediaPlaybackFragment.isMediaPlaying()) {
                    mMediaPlaybackFragment.pause();
                } else {
                    mMediaPlaybackFragment.resume();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (mMediaPlaybackFragment.isMediaPlaying()) {
                    mMediaPlaybackFragment.pause();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (!mMediaPlaybackFragment.isMediaPlaying()) {
                    mMediaPlaybackFragment.resume();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if ((mViewPager.getCurrentItem() == 0 || mViewPager.getCurrentItem() == 2)) {
                    if (mMediaPlaybackFragment.isPlayingMode()) {
                        mViewPager.setCurrentItem(1, true);
                    } else {
                        this.finish();
                    }
                } else {
                    this.finish();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
    
    private void initAutoPlay(Intent intent) {
        Log.d(TAG, "=initAutoPlay=");
        // get data from intent
        Uri uri = intent.getData();
        if (uri != null) {
            // play from filemanager
            play(uri);
            MediaModel.getInstance().startFolderVideoLoader(uri, mFolderLoadListener);
        } else {
            // play from memory
            boolean all = mStoreManager.getBoolean(StoreManager.KEY_ALL_VIDEO, false);
            uri = Uri.parse(mStoreManager.getString(StoreManager.KEY_CUR_URI, ""));
            Log.d(TAG,  "=>restore uri = " + uri);
            Store store = mStoreManager.getMediaStore(uri);
            File file = new File(uri.getPath());
            if (store != null && store.mounted() && file.exists()) {
                play(uri);
                if (all) {
                    ArrayList<VideoEntry> list = MediaModel.getInstance().startAllVideoLoader(store, mAllLoadListener);
                    if (list != null && list.size() > 0) {
                        if (!mAutoPlay) {
                            mAutoPlay = true;
                            ArrayList<Uri> arrayList = new ArrayList<Uri>();
                            for (int i = 0; i < list.size(); i ++) {
                                arrayList.addAll(list.get(i).mVideoList);
                            }
                            switchToPlay(store, arrayList, true);
                            Log.d(TAG, "initAutoPlay restor from all to auto play");
                        }
                    }
                } else {
                    MediaModel.getInstance().startFolderVideoLoader(uri, mFolderLoadListener);
                }
            } else {
                // switch to default play.
                ArrayList<Store> stores = mStoreManager.getStoreList();
                mAutoPlay = false;
                for(Store mediaStore : stores) {
                    if (mediaStore.mounted()) {
                        ArrayList<VideoEntry> list = MediaModel.getInstance().startAllVideoLoader(mediaStore, mAllLoadListener);
                        if (list != null) {
                            if (list.size() > 0) {
                                if (!mAutoPlay) {
                                    mAutoPlay = true;
                                    ArrayList<Uri> arrayList = new ArrayList<Uri>();
                                    for (int i = 0; i < list.size(); i ++) {
                                        arrayList.addAll(list.get(i).mVideoList);
                                    }
                                    switchToPlay(mediaStore, arrayList, true);
                                    Log.d(TAG, "initAutoPlay set all to auto play");
                                }
                                break;
                            } else {
                                if (mediaStore.getStorageType() == Store.TYPE_HD && !mAutoPlay) {
                                    switchToPage(0);
                                    mMediaStorageFragment.setCurrentMediaStore(mediaStore);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mFragments.add(mMediaStorageFragment);
        mFragments.add(mMediaPlaybackFragment);
        mFragments.add(mMediaPlaylistFragment);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }

            @Override
            public float getPageWidth(int position) {
                if (position == 2) {
                    return super.getPageWidth(position) / 3;
                } else {
                    return super.getPageWidth(position);
                }
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1, true);
    }
    
    public void play(int index) {
        mMediaPlaybackFragment.play(index);
    }
    
    public void play(Uri uri) {
        mMediaPlaybackFragment.play(uri);
    }
    
    public void switchToPlay(Store store, ArrayList<Uri> fileUriList, boolean all) {
        if (all) {
            mStoreManager.putBoolean(StoreManager.KEY_ALL_VIDEO, true);
        } else {
            mStoreManager.putBoolean(StoreManager.KEY_ALL_VIDEO, false);
        }
        mViewPager.setCurrentItem(1, true);
        mMediaStorageFragment.setCurrentMediaStore(store);
        mMediaPlaybackFragment.registerPositionListener(mMediaPlaylistFragment.mPositionChangedListener);
        mMediaPlaylistFragment.setDataList(fileUriList);
        mMediaPlaybackFragment.listPlay(store, fileUriList);
    }
    
    public void switchToPage(int page) {
        mViewPager.setCurrentItem(page, true);
    }
    
    public void enterFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    }
    
    public void quitFullScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                mMediaPlaybackFragment.setCurrentPage(0);
                quitFullScreen();
            } else if (arg0 == 2) {
                mMediaPlaybackFragment.setCurrentPage(2);
                mMediaPlaybackFragment.showBars(mMediaPlaybackFragment.DEFAULT_TIMEOUT);
            } else {
                mMediaPlaybackFragment.setCurrentPage(1);
                mMediaPlaybackFragment.showBars(mMediaPlaybackFragment.DEFAULT_TIMEOUT);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    
    private MediaPlaylistFragment.OnReadyListener mReadyListener = new MediaPlaylistFragment.OnReadyListener() {
        public void onReadyAutoPlay() {
            mHandler.sendEmptyMessage(MSG_AUTO_PLAY);
        }
    };
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_PLAY :
                    initAutoPlay(getIntent());
                    break;
                default:
                    break;
            }
        }
        
    };
    
    private MediaModel.OnAllVideoLoadListener mAllLoadListener = new MediaModel.OnAllVideoLoadListener() {
        
        @Override
        public void onLoadFinished(Store store, ArrayList<VideoEntry> dataList) {
            if (!isFinishing() && !isDestroyed()) {
                if (dataList != null && dataList.size() > 0) {
                    if (!mAutoPlay) {
                        mAutoPlay = true;
                        ArrayList<Uri> arrayList = new ArrayList<Uri>();
                        for (int i = 0; i < dataList.size(); i ++) {
                            arrayList.addAll(dataList.get(i).mVideoList);
                        }
                        switchToPlay(store, arrayList, true);
                        Log.d(TAG, "onLoadFinished set all to auto play");
                    }
                } else {
                    // last one, has no data
                    if (store.getStorageType() == Store.TYPE_HD && !mAutoPlay) {
                        switchToPage(0);
                        mMediaStorageFragment.setCurrentMediaStore(store);
                    }
                }
            }
        }
    };
    
    private MediaModel.OnFolderVideoLoadListener mFolderLoadListener = new MediaModel.OnFolderVideoLoadListener() {
        
        @Override
        public void onLoadFinished(Uri uri, ArrayList<Uri> dataList) {
            if (!isFinishing() && !isDestroyed()) {
                if (dataList != null && dataList.size() > 0) {
                    Store store = mStoreManager.getMediaStore(dataList.get(0));
                    switchToPlay(store, dataList, false);
                }
            }
        }
    };

}
