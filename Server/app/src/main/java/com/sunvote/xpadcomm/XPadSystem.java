package com.sunvote.xpadcomm;


import com.sunvote.xpadapp.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XPadSystem {
	private static String TAG="";
	public static void setStatusBarSingal(Context context,int rssi) {
		String actionName = null;
		// Log.d(TAG, "setStatusBarSingal:"+level);
		
		int level =0;
		if (rssi < 95)
			level = 1;
		if (rssi < 85)
			level = 2;
		if (rssi < 75)
			level = 3;
		if (rssi < 65)
			level = 4;
		if (rssi < 55)
			level = 5;
		if (rssi == 0)
			level = 0;
		
		switch (level) {
		case 0:
			actionName = "com.along.intent.ZERO";
			break;
		case 1:
			actionName = "com.along.intent.ONE";
			break;
		case 2:
			actionName = "com.along.intent.TWO";
			break;
		case 3:
			actionName = "com.along.intent.THREE";
			break;
		case 4:
			actionName = "com.along.intent.FOUR";
			break;
		case 5:
			actionName = "com.along.intent.FIVE";
			break;

		default:
			break;
		}
		Intent intent = new Intent();
		intent.setAction(actionName);
		context.sendBroadcast(intent);
		//setStatusBarDataIcon(context,3);
	}

	public static void setStatusBarDataIcon(Context context,int flag) {
		String actionName = null;
		switch (flag) {
		case 0:
			actionName = "com.along.intent.BACK";
			break;
		case 1:
			actionName = "com.along.intent.UP";
			break;
		case 2:
			actionName = "com.along.intent.DOWN";
			break;
		case 3:
			actionName = "com.along.intent.LIGHT";
			break;

		default:
			break;
		}
		Intent intent = new Intent();
		intent.setAction(actionName);
		context.sendBroadcast(intent);
	}
	
	public static void setStatusBarDataIcon(Context context,int tx,int rx) {
		String actionName = null;
		int flag = 0;
		if (tx == 0 && rx == 0) {
			flag=0;
		} else if ( tx == 1 &&  rx == 0) {
			flag=1;
		} else if (tx == 0 && rx == 1) {
			flag=2;
		} else if (tx == 1 && rx == 1) {
			flag=3;
		}
		switch (flag) {
		case 0:
			actionName = "com.along.intent.BACK";
			break;
		case 1:
			actionName = "com.along.intent.UP";
			break;
		case 2:
			actionName = "com.along.intent.DOWN";
			break;
		case 3:
			actionName = "com.along.intent.LIGHT";
			break;

		default:
			break;
		}
		Intent intent = new Intent();
		intent.setAction(actionName);
		context.sendBroadcast(intent);
	}


	public static void setStatusBarBaseId(Context context,String info) {
		// Log.d(TAG, "set statusbar: "+info);
		Intent intent = new Intent();
		intent.putExtra("company_name_Intent", info);
		intent.setAction("com.along.intent.COMPANY_NAME");
		context.sendBroadcast(intent);

	}

	public static void setStatusBarChannel(Context context,int ch) {
		Intent intent = new Intent();
		intent.putExtra("table_channel_Intent", String.valueOf(ch));
		intent.setAction("com.along.intent.CHANGE_CHANNEL");
		context.sendBroadcast(intent);
	}

	public static void setStatusBarPadID(Context context,int padid) {
		Intent intent = new Intent();
		intent.putExtra("table_id_Intent", context.getString(R.string.terminal_id) + padid);
		intent.setAction("com.along.intent.CHANGE_TABLE_ID");
		context.sendBroadcast(intent);
	}

	public static void powerOffXPad(Context context) {

		Intent intent = new Intent();
		intent.setAction("com.along.intent.SHUTDOWN");
		context.sendBroadcast(intent);
//		return;
//
//		try {
//			Log.d(TAG, "powerOffXPad");
//			Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//			intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(intent);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static void rebootXPad(Context context) {

		Intent intent = new Intent();
		intent.setAction("com.along.intent.REBOOT");
		context.sendBroadcast(intent);

		//com.along.intent.REBOOT
//		try {
//			Log.d(TAG, "reBootXPad");
//			Intent intent2 = new Intent(Intent.ACTION_REBOOT);
//			intent2.putExtra("nowait", 1);
//			intent2.putExtra("interval", 1);
//			intent2.putExtra("window", 0);
//			context.sendBroadcast(intent2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static void setNavgationVisible(Context context){
		Log.d(TAG, "setNavgationVisible");
		Intent intent = new Intent();
		intent.setAction("com.along.intent.Navigation_VISIBLE");
		context.sendBroadcast(intent);
	}
	
	public static void setNavgationGone(Context context){
		Log.d(TAG, "setNavgationGone");
		Intent intent = new Intent();
		intent.setAction("com.along.intent.Navigation_GONE");
		context.sendBroadcast(intent);
	}
	
	public static void goToSleep(Context context){
		Log.d(TAG, "goToSleep");
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SCREEN_OFF");
		context.sendBroadcast(intent);
	}
	
}
