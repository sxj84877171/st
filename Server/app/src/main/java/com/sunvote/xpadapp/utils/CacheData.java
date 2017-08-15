package com.sunvote.xpadapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



public class CacheData {
	private static SharedPreferences sp;  
    
    @SuppressWarnings("static-access")  
    public static void SetData(Context context, String filename, String key,  
            String value) {  
        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);  
        Editor editor = sp.edit();  
        editor.putString(key, value);  
        editor.commit();  
    }  
  
    @SuppressWarnings("static-access")  
    public static String GetData(Context context, String filename, String key) {  
        String value = "";  
        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);  
        value = sp.getString(key, "");  
        return value;  
    }  
}
