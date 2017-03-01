package com.example.sample2_6;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        connection=new ServiceConnection(){		
            public void onServiceConnected(ComponentName arg0, IBinder arg1) {}			
            public void onServiceDisconnected(ComponentName arg0) {}
        	
        };
        startService=(Button)findViewById(R.id.startService);
        stopService=(Button)findViewById(R.id.stopService);
        bindService=(Button)findViewById(R.id.bindService);
        unbindService=(Button)findViewById(R.id.unbindService);
        myOnClickListener=new OnClickListener(){

		//	public void onClick(View v) {
			public void onClick(DialogInterface arg0, int arg1) {	
			      View v = null;
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
        
        startService.setOnClickListener((android.view.View.OnClickListener) myOnClickListener);
        stopService.setOnClickListener((android.view.View.OnClickListener) myOnClickListener);
        bindService.setOnClickListener((android.view.View.OnClickListener) myOnClickListener);
        unbindService.setOnClickListener((android.view.View.OnClickListener) myOnClickListener);
        
    }

   
}
