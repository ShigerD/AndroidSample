package com.clw.video;

import java.io.File;

import android.net.Uri;

public interface Store {

    public static int TYPE_HD  = 0x00;
    public static int TYPE_SD  = 0x01;
    public static int TYPE_USB = 0x02;
    
    public Uri getUri();
    public int getStorageType();
    public File getDirectory();
    public boolean mounted();
    
}
