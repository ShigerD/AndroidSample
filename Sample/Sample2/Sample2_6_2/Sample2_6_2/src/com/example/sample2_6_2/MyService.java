package com.example.sample2_6_2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service{
	MyThread mythread;
    
	public void onCreat(){
		Log.d("MyService","onCreate");
		super.onCreate();
	}
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent,int startID){
		Log.d("MyService", "onStart");
		if(mythread==null){
			mythread= new MyThread();
			mythread.start();
		}
		
		super.onStart(intent, startID);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("MyService", "onbind");
		if(mythread==null){
			mythread= new MyThread();
			mythread.start();
		}		
		return null;
	}
	
	public boolean onUbind(Intent intent){
		Log.d("MyService", "onUnbind");
		if(mythread!=null){
			mythread.flag=false;
			mythread=null;
		}
		return super.onUnbind(intent);
		
	}
	
	public void onDestroy(){
		Log.d("Myservice", "ondestory");
		if(mythread!=null){
			mythread.flag=false;
			mythread=null;
		}
		super.onDestroy();
	}

}
