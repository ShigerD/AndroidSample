package com.example.sound2;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
  SoundPool sound;
  HashMap<Integer,Integer> soundpoolmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
        initSounds();
        
        setContentView(R.layout.activity_main);
        int i=0;
        
        playSound(1,3);
        
        
    }
 
    public void initSounds(){
    	sound=new SoundPool(4,AudioManager.STREAM_MUSIC,100);
    	soundpoolmap=new HashMap<Integer,Integer>();
    	soundpoolmap.put(1, sound.load(this,R.raw.ak47,1));  
    }
    
    public void playSound(int s,int loop){

    	
    	AudioManager mgr=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
    	float streamVolumeCurrent=mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    	float streamVolumeMax=mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	//float volume=streamVolumeCurrent/streamVolumeMax;
    	float volume=streamVolumeMax;
    	int max=(int) streamVolumeMax;
    	
   	    mgr.setStreamVolume(AudioManager.STREAM_MUSIC,max , 0);
    	
    	sound.play(soundpoolmap.get(s),10,10,1,loop,1f);
    
    }
   
}
