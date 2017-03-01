package com.example.broadcastreceive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context content, Intent intent) {
		// TODO Auto-generated method stub
		Intent i=new Intent(content,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		content.startActivity(i);
	}
	

}
