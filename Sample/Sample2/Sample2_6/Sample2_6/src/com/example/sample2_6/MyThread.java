package com.example.sample2_6;

import android.util.Log;

public class MyThread extends Thread{
	boolean flag=true;
	int i=0;
	public void run(){
		while (flag){
			Log.d( "MyService", "i="+(i++));
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
