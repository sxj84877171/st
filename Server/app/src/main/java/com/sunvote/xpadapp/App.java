package com.sunvote.xpadapp;

import java.util.List;

import com.sunvote.xpadcomm.XPadApi;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;

public class App extends Application {
	public static XPadApi XPad;
	
	@Override
	public void onCreate() {
		 XPad = XPadApi.getInstance();
		super.onCreate();
	}
	
	public static boolean isBackground(Context context) {
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                         // Log.i("��̨", appProcess.processName);
	                          return true;
	                }else{
	                         // Log.i("ǰ̨", appProcess.processName);
	                          return false;
	                }
	           }
	    }
	    return false;
	}
}
