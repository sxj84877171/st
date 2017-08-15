package com.sunvote.xpadapp.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class BaseActivity extends FragmentActivity {
	
	private String TAG = "BaseActivity";
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
		
		//禁止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		  
		super.onCreate(savedInstanceState);
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    Log.i(TAG,"onKeyDown keycode="+keyCode );  
	    switch (keyCode) {  
	        case KeyEvent.KEYCODE_BACK:  
	        Log.i(TAG,"KEYCODE_BACK");  
	        return true;  
	        case KeyEvent.KEYCODE_HOME:
                return true;
         
	    }  
	    return super.onKeyDown(keyCode, event);  
	}

	@Override
	public void onAttachedToWindow() {
		 
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}



	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
           //返回true，不响应其他key
	 int keyCode=	event.getKeyCode();
		 Log.i(TAG,"dispatchKeyEvent keycode="+keyCode );  
		    switch (keyCode) {  
		        case KeyEvent.KEYCODE_BACK:  
		        Log.i(TAG,"KEYCODE_BACK");  
		        return true;  
		        case KeyEvent.KEYCODE_HOME:
		        	if(event.isLongPress()){
		        		moveTaskToBack(false); 
		        	}
	                return true;
	         
		    }  
           return true;
    }



}
