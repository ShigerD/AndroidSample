package com.example.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	Button btnBegin;
	Button btnStop;
	public static TextView mytv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBegin=(Button)findViewById(R.id.button1);
        btnStop=(Button)findViewById(R.id.button2);
        mytv=(TextView)findViewById(R.id.tv1);
        btnBegin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				// TODO Auto-generated method stub
				Intent i=new Intent(MainActivity.this,Myservice.class);
				startService(i);
				Toast.makeText(MainActivity.this,"service启动成功", Toast.LENGTH_LONG).show();	
			}
        });
        btnStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				// TODO Auto-generated method stub
				Intent i=new Intent(MainActivity.this,Myservice.class);
				stopService(i);
				Toast.makeText(MainActivity.this,"service停止成功", Toast.LENGTH_LONG).show();	
			}
        });
        IntentFilter intentFilter =new IntentFilter("com.example.service.myThread");//com.example.service.myThread
        MyBroadcasReceiver myBR=new MyBroadcasReceiver();
        registerReceiver(myBR,intentFilter);
        
    }
    public class MyBroadcasReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context content	, Intent intent) {
			// TODO Auto-generated method stub
			Bundle myBundle=intent.getExtras();
			int myInt=myBundle.getInt("myThread");
			if(myInt<10){
				mytv.setText("后台Service运行了"+myInt+"s\n");
			}else {
				Intent i=new Intent(MainActivity.this,Myservice.class);
				stopService(i);
				mytv.setText("后台Service在"+myInt+"s后停止");
			}
		}
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
