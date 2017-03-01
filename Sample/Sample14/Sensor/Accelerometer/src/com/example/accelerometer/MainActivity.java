package com.example.accelerometer;

import android.app.Activity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;


public class MainActivity extends Activity {

	TextView MyTextView1;
	TextView MyTextView2;
	TextView MyTextView3;
	SensorManager mySensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTextView1=(TextView)findViewById(R.id.textView1);
        MyTextView2=(TextView)findViewById(R.id.textView2);
        MyTextView3=(TextView)findViewById(R.id.textView3);
        mySensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
     
    }
    @SuppressWarnings("deprecation")
	private SensorListener mySensorListener=new SensorListener(){

		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {}

		@Override
		public void onSensorChanged(int sensor, float[] values) {
			
			if(sensor==SensorManager.SENSOR_ACCELEROMETER){
				MyTextView1.setText("x::"+values[0]);
				MyTextView2.setText("y::"+values[1]);
				MyTextView3.setText("z::"+values[2]);
			}
		}   	
    };
    @SuppressWarnings("deprecation")
	protected void onResume(){
    	mySensorManager.registerListener(
    			mySensorListener, 
    			SensorManager.SENSOR_ACCELEROMETER,
    			SensorManager.SENSOR_DELAY_UI);
    	super.onResume();
    }
    @SuppressWarnings("deprecation")
	protected void pause(){
    	mySensorManager.unregisterListener(mySensorListener);
    	super.onPause();
    }
}
