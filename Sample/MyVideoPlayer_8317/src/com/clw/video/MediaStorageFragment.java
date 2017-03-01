
package com.clw.video;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MediaStorageFragment extends Fragment {
    
    private static final String TAG = MediaStorageFragment.class.getSimpleName();
    
    private StoreManager mStoreManager;
    private Store mCurrentStore;
    
    private MediaFragment mMediaFragmentHD, mMediaFragmentUSB, mMediaFragmentSD;
    private RadioButton mRadioBtnHD, mRadioBtnUSB, mRadioBtnSD;
    private RadioGroup mRaidoGroup;
    private MediaPlaybackFragment mPlayback;
    private MediaPlaylistFragment mPlaylist;
    
    private int mStateRecord = 0;
    private static final int STATE_FORGROUND = 0x00000001;

    public void setMediaPlaybackFragment(MediaPlaybackFragment playback) {
        this.mPlayback = playback;
    }
    
    public void setMediaPlaylistFragment(MediaPlaylistFragment playlist) {
        this.mPlaylist = playlist;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "=>onActivityCreated=");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "=>onAttach=");
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "=>onDetach=");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoreManager = StoreManager.getInstance(getActivity());
        mStoreManager.registerStoreChangedListener(mStoreChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=>onDestroy=");
        mStoreManager.unregisterStoreChangedListener(mStoreChangedListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "=>onDestroyView=");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "=>onPause=");
        mStateRecord &= (~STATE_FORGROUND);
        mRaidoGroup.setOnCheckedChangeListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=>onResume=");
        mStateRecord |= STATE_FORGROUND;
        mRaidoGroup.setOnCheckedChangeListener(OnCheckedChangeListener);
        updateStorageState();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "=>onStart=");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "=>onStop=");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "=>onCreateView=");
        View view = inflater.inflate(R.layout.storage_layout, container, false);
        mRaidoGroup = (RadioGroup)view.findViewById(R.id.radio_group);
        mRadioBtnHD = (RadioButton)mRaidoGroup.findViewById(R.id.storage_hd);
        mRadioBtnUSB = (RadioButton)mRaidoGroup.findViewById(R.id.storage_usb);
        mRadioBtnSD = (RadioButton)mRaidoGroup.findViewById(R.id.storage_sd);
        return view;
    }
    
    public void setCurrentMediaStore(Store store) {
        mCurrentStore = store;
        switch (store.getStorageType()) {
            case Store.TYPE_HD:
                mRadioBtnHD.setChecked(true);
                break;
            case Store.TYPE_SD:
                mRadioBtnSD.setChecked(true);
                break;
            case Store.TYPE_USB:
                mRadioBtnUSB.setChecked(true);
                break;
        }
    }
    
    private void updateStorageState() {
        ArrayList<Store> stores =  mStoreManager.getStoreList();
        for (Store store: stores) {
            if (!store.mounted()) {
                MediaModel.getInstance().resetData(store);
                if (store.equals(mPlayback.getPlayingStore())) {
                    // eject current playing store. stop current playback
                    Log.d(TAG, "=stop current playback.");
                    VideoTabActivity activity = (VideoTabActivity)getActivity();
                    activity.switchToPage(0);
                    mPlayback.stop();
                    mPlaylist.setDataList(null);
                    mPlayback.clearPlayList();
                }
                
                if (store.equals(mCurrentStore)) {
                    if ((mStateRecord & STATE_FORGROUND) != 0) {
                        // eject current! switch to last tab.
                        Log.d(TAG, "=switch to last tab.");
                        for (Store s : stores) {
                            if (s.mounted()) {
                                setCurrentMediaStore(s);
                                break;
                            }
                        }
                    } else {
                        // background eject current store. finish it background;
                        Log.d(TAG, "=finish it background.");
                        getActivity().finish();
                    }
                }
            }
            switch (store.getStorageType()) {
                case Store.TYPE_HD:
                    mRadioBtnHD.setVisibility(store.mounted() ? View.VISIBLE : View.GONE);
                    break;
                case Store.TYPE_SD:
                    mRadioBtnSD.setVisibility(store.mounted() ? View.VISIBLE : View.GONE);
                    break;
                case Store.TYPE_USB:
                    mRadioBtnUSB.setVisibility(store.mounted() ? View.VISIBLE : View.GONE);
                    break;
            }
        }
    }
    
    private void ejectStoragePlaying(Uri storageVolume) {
        Store store = mStoreManager.getMediaStore(storageVolume);
        if (store.getStorageType() == Store.TYPE_USB && store.mounted()) {
            if (store.equals(mPlayback.getPlayingStore())) {
                // eject current playing store. stop current playback
                Uri playingUri = mPlayback.getPlayingUri();
                if (playingUri.getPath().startsWith(storageVolume.getPath())) {
                    Log.d(TAG, "=stop current playback.");
                    VideoTabActivity activity = (VideoTabActivity)getActivity();
                    activity.switchToPage(0);
                    mPlayback.stop();
                    mPlaylist.setDataList(null);
                } else {
                    if (mStoreManager.getBoolean(StoreManager.KEY_ALL_VIDEO, false)) {
                        ArrayList<VideoEntry> list = MediaModel.getInstance().getAllVideoList(store);
                        if (list != null && list.size() > 0) {
                            ArrayList<Uri> arrayList = new ArrayList<Uri>();
                            for (int i = 0; i < list.size(); i ++) {
                                arrayList.addAll(list.get(i).mVideoList);
                            }
                            mPlayback.listPlay(store, arrayList);
                            mPlaylist.setDataList(arrayList);
                        }
                    }
                }
            }
        }
    }
    
    private RadioGroup.OnCheckedChangeListener OnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup arg0, int arg1) {
            int radioButtonId = arg0.getCheckedRadioButtonId();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            switch (radioButtonId) {
                case R.id.storage_hd:
                    Log.d(TAG, "===>>HD ");
                    mCurrentStore = mStoreManager.getMediaStore(Store.TYPE_HD);
                    if (mMediaFragmentHD == null) {
                        mMediaFragmentHD = new MediaFragment(mCurrentStore);
                    }
                    transaction.replace(R.id.container, mMediaFragmentHD);
                    transaction.commit();
                    break;
                case R.id.storage_usb:
                    Log.d(TAG, "===>>USB ");
                    mCurrentStore = mStoreManager.getMediaStore(Store.TYPE_USB);
                    if (mMediaFragmentUSB == null) {
                        mMediaFragmentUSB = new MediaFragment(mCurrentStore);
                    }
                    transaction.replace(R.id.container, mMediaFragmentUSB);
                    transaction.commit();
                    break;
                case R.id.storage_sd:
                    Log.d(TAG, "===>>SD ");
                    mCurrentStore = mStoreManager.getMediaStore(Store.TYPE_SD);
                    if (mMediaFragmentSD == null) {
                        mMediaFragmentSD = new MediaFragment(mCurrentStore);
                    }
                    transaction.replace(R.id.container, mMediaFragmentSD);
                    transaction.commit();
                    break;
                default:
                    transaction.commit();
                    break;
            }
        }
    };

    private StoreManager.IStoreChangedListener mStoreChangedListener = new StoreManager.IStoreChangedListener() {
        public void onStoreChanged(Uri storageVolume, boolean mounted) {
            updateStorageState();
            if (!mounted) {
                ejectStoragePlaying(storageVolume);
            }
        }
    };
}
