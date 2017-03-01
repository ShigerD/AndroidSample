package com.clw.video;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaStoreUdisk extends MediaStoreBase {
    
    public Uri getUri() {
        return MediaStore.Video.Media.EXTERNAL_UDISK_CONTENT_URI;
    }
    public int getStorageType() {
        return TYPE_USB;
    }
    
    public File getDirectory() {
        return Environment.getExternalStorageUdiskDirectory();
    }
    
    public boolean mounted() {
        if (Environment.getExternalStorageUdiskState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
