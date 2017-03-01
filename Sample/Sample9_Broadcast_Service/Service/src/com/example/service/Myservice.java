package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Myservice extends Service{
	MyThread myThread;
	public void onCreate(){
		Log.d("myservice", "oncreate");
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	//
	public void onDestory(){
		myThread.flag=false;
		super.onDestroy();
	}
	//
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent,int startId){
		Log.d("myservice", "oncreate");
		myThread=new MyThread();
		myThread.start();
		super.onStart(intent, startId);
		
		MainActivity.mytv.setText("super.onStart(intent, startId);");
	}
	/*
	 * 
	 */
	 class MyThread extends Thread{
		 boolean flag=true;
		 int c=0;
		 public void run(){
			 while(flag){
				 Intent i=new Intent("com.example.service.myThread");
				 i.putExtra("myThread", c);
				 sendBroadcast(i);
				 c++;
				 try{
					 Thread.sleep(1000);
					 System.out.print(c);
				 }catch(Exception e){
					 e.printStackTrace() ;
				 }
			 }
		 }

	 }
}
/*
*/
