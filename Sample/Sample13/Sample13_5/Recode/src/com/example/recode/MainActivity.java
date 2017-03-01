package com.example.recode;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class MainActivity extends Activity {
 
	EditText mystate;
	Button start;
	Button stop;
	File myfile;
	MediaRecorder myrecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=(Button )findViewById(R.id.start);
        stop=(Button )findViewById(R.id.stop);
        
mystate=(EditText)findViewById(R.id.editText1);
        
        start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			
				if(v==start){
					mystate.setText("enter1");
		    		if(!Environment.getExternalStorageState()
		    				.equals(android.os.Environment.MEDIA_MOUNTED)){
		    			Toast.makeText(MainActivity.this, "Çë¼ì²âÄÚ´æ¿¨", Toast.LENGTH_LONG).show();
		    			mystate.setText("out1");
		    			return;
		    		}
		    		try{
		    			myfile=File.createTempFile("Sample13_5", ".arm",
		    					Environment.getExternalStorageDirectory());		
		    			myrecorder=new MediaRecorder();
		    			myrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    			myrecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		    			myrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		    			myrecorder.setOutputFile(myfile.getAbsolutePath());
		    			myrecorder.prepare();
		    			myrecorder.start();
		    			mystate.setText("enter2");
		    		}catch (IOException e){
		    			e.printStackTrace();
		    		}
			}
        	
			}
        });
        stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(v==stop){
		    		if(myfile!=null){
		    			myrecorder.stop();
		    			myrecorder.release();
		    			myrecorder=null;
		    			mystate.setText("finish1");
		    		}
				
			}
			}
        });
    }

}
