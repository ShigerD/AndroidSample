package com.clw.video;

import java.io.File;

import android.net.Uri;

public class MediaStoreBase implements Store {
    
    public Uri getUri() {
        return null;
    }
    
    public int getStorageType() {
        return TYPE_HD;
    }
    
    public File getDirectory() {
        return null;
    }
    
    public boolean mounted() {
        return false;
    }
    
    public String toString() {
        if (getDirectory() == null) {
            return String.valueOf(getStorageType());
        } else {
            return getDirectory().getPath();
        }
    }
    
}
