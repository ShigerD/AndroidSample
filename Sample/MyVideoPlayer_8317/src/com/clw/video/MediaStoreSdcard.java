package com.clw.video;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaStoreSdcard extends MediaStoreBase {
    
    public Uri getUri() {
        return MediaStore.Video.Media.EXTERNAL_SD_CONTENT_URI;
    }
    public int getStorageType() {
        return TYPE_SD;
    }
    
    public File getDirectory() {
        return Environment.getExternalStorageSdcardDirectory();
    }
    
    public boolean mounted() {
        if (Environment.getExternalStorageSdcardState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
