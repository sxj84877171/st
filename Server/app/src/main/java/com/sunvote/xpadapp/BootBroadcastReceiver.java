package com.sunvote.xpadapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 
		Log.d("BootBroadcastReceiver","boot serv start.....");  

		 Intent i = new Intent(context, MainActivity.class); 
		 i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		 context.startActivity(i);            
	}

}
