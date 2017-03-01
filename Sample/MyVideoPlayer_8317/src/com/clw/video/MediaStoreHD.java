package com.clw.video;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaStoreHD extends MediaStoreBase {
    
    public Uri getUri() {
        return MediaStore.Video.Media.EXTERNAL_INAND_CONTENT_URI;
    }
    
    public int getStorageType() {
        return TYPE_HD;
    }
    
    public File getDirectory() {
        return Environment.getExternalStorageINandDirectory();
    }
    
    public boolean mounted() {
        if (Environment.getExternalStorageINandState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
