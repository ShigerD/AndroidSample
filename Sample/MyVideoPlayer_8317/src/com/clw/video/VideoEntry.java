package com.clw.video;

import java.io.File;
import java.util.ArrayList;

import android.net.Uri;

public class VideoEntry {
    
    public File mDir;
    public ArrayList<Uri> mVideoList = new ArrayList<Uri>();
    
    public VideoEntry(File dir) {
        mDir = dir;
    }
    
    public void addVideo(Uri fileUri) {
        mVideoList.add(fileUri);
    }
    
    public void clear() {
        mVideoList.clear();
    }
}
