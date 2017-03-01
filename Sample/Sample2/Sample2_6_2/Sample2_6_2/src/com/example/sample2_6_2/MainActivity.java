package com.example.sample2_6_2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	OnClickListener myOnClickListener;
	ServiceConnection connection;
	Button startService;
	Button stopService;
	Button bindService;
	Button unbindService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	Log.e("mainActivity", "onbind");
    	
        connection=new ServiceConnection(){		
            public void onServiceConnected(ComponentName arg0, IBinder arg1) {}			
            public void onServiceDisconnected(ComponentName arg0) {}
        	
        };
        startService=(Button)findViewById(R.id.startService);
        stopService=(Button)findViewById(R.id.stopService);
        bindService=(Button)findViewById(R.id.bindService);
        unbindService=(Button)findViewById(R.id.unbindService);
        myOnClickListener=new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,MyService.class);
				if(v==startService){
					startService(intent);
					bindService.setEnabled(false);
					unbindService.setEnabled(false);
				}else if(v==stopService){
					stopService(intent);
					bindService.setEnabled(true);
					unbindService.setEnabled(true);					
				}else if(v==bindService){
					bindService(intent,connection,BIND_AUTO_CREATE);
					startService.setEnabled(false);
					stopService.setEnabled(false);
					
				}else if(v==unbindService){
					unbindService(connection);
					startService.setEnabled(true);
					stopService.setEnabled(true);					
				}
			}    	
       };
        
       startService.setOnClickListener (myOnClickListener);
       stopService.setOnClickListener  (myOnClickListener);
       bindService.setOnClickListener  (myOnClickListener);
       unbindService.setOnClickListener(myOnClickListener);
        
    }

}
