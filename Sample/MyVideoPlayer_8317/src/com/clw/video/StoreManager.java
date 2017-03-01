package com.clw.video;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

public class StoreManager {
    private static final String TAG = StoreManager.class.getSimpleName();
    
    private static StoreManager sStoreManager = null;
    
    public static final String STORE_NAME           = "VIDEO";
    public static final String KEY_STORE_TYPE       = "KEY_STORE_TYPE";
    public static final String KEY_SCREEN_MODE      = "KEY_SCREEN_MODE";
    public static final String KEY_CUR_URI          = "KEY_CUR_URI";
    public static final String KEY_DUR_POSITION     = "KEY_DUR_POSITION";
    public static final String KEY_ALL_VIDEO        = "KEY_ALL_VIDEO";
    
    private Context mContext;
    private SharedPreferences mPref;
    private ArrayList<Store> mStoreList = new ArrayList<Store>();
    private ArrayList<IStoreChangedListener> mListeners = new ArrayList<IStoreChangedListener>();
    
    public static synchronized StoreManager getInstance(Context context) {
        if (sStoreManager == null) {
            sStoreManager = new StoreManager(context);
        }
        return sStoreManager;
    }
    
    private StoreManager(Context context) {
        mContext = context;
        mStoreList.add(new MediaStoreSdcard());
        mStoreList.add(new MediaStoreUdisk());
        mStoreList.add(new MediaStoreHD());
        mPref = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        //intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        //intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        //intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        mContext.getApplicationContext().registerReceiver(mReceiver, intentFilter);
    }
    
    public void registerStoreChangedListener(IStoreChangedListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        } else {
            Log.d(TAG, "register same listener, ignore it");
        }
    }
    
    public void unregisterStoreChangedListener(IStoreChangedListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        } else {
            Log.d(TAG, "unregister does not existed listener");
        }
    }
    
    public ArrayList<Store> getStoreList() {
        return mStoreList;
    }
    
    public Store getMediaStore(int mediaType) {
        for(Store store : mStoreList) {
            if (store.getStorageType() == mediaType) {
                return store;
            }
        }
        return null;
    }
    
    public Store getMediaStore(Uri fileUri) {
        String filePath = fileUri.getPath();
        for(Store store : mStoreList) {
            if(filePath.startsWith(store.getDirectory().getPath())) {
                return store;
            }
        }
        return null;
    }
    
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean mounted = false;
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                mounted = true;
            } else {
                mounted = false;
            }
            
            Uri storageVolume = intent.getData();
            MediaModel.getInstance().updateData(getMediaStore(storageVolume), storageVolume, mounted);
            for(IStoreChangedListener listener : mListeners) {
                listener.onStoreChanged(storageVolume, mounted);
            }
        }
    };
    
    public interface IStoreChangedListener {
        public void onStoreChanged(Uri storageVolume, boolean mounted);
    }
    
}
