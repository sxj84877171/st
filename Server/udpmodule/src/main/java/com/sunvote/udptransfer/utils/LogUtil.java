package com.sunvote.udptransfer.utils;

import android.util.Log;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:日志类
 */

public class LogUtil {

    public static final int VERBOSE_LEVER = 2;
    public static final int DEBUG_LEVER = 3;
    public static final int INFO_LEVER = 4;
    public static final int WARN_LEVER = 5;
    public static final int ERROR_LEVER = 6;
    public static final int ASSERT_LEVER = 7;

    public static int lever = VERBOSE_LEVER - 1 ;

    LogUtil() {
        throw new RuntimeException("Stub!");
    }

    public static int v(String tag, String msg) {
        if (VERBOSE_LEVER > lever) {
           return Log.v(tag, msg);
        }
        return -1;
    }

    public static void enableLog(){
        lever = VERBOSE_LEVER - 1;
    }

    public static void disableLog(){
        lever = ASSERT_LEVER ;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if(VERBOSE_LEVER > lever){
            return Log.v(tag,msg,tr);
        }
        return -1;
    }

    public static int d(String tag, String msg) {
        if(DEBUG_LEVER > lever){
            return Log.d(tag,msg);
        }
        return -1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if(DEBUG_LEVER > lever){
            return Log.d(tag,msg,tr);
        }
        return -1;
    }

    public static int i(String tag, String msg) {
        if(INFO_LEVER > lever){
            return Log.i(tag,msg);
        }
        return -1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if(INFO_LEVER > lever){
            return Log.i(tag,msg,tr);
        }
        return -1;
    }

    public static int i(String tag,byte[] msg){
        String msgStr = ByteUtils.bytesToHexString(msg);
        return i(tag,msgStr);
    }

    public static int i(String tag,String msgTag, byte[] msg){
        String msgStr = ByteUtils.bytesToHexString(msg);
        return i(tag,msgTag + ":\r\n" + msgStr);
    }

    public static int v(String tag,String msgTag, byte[] msg){
        String msgStr = ByteUtils.bytesToHexString(msg);
        return v(tag,msgTag + ":\r\n" + msgStr);
    }

    public static int i(String tag,String msgTag, byte[] msg,int length){
        String msgStr = ByteUtils.bytesToHexString(msg,length);
        return i(tag,msgTag + ":\r\n" + msgStr);
    }

    public static int v(String tag,String msgTag, byte[] msg,int length){
        String msgStr = ByteUtils.bytesToHexString(msg,length);
        return v(tag,msgTag + ":\r\n" + msgStr);
    }

    public static int i(String tag,byte[] msg,Throwable tr){
        String msgStr = ByteUtils.bytesToHexString(msg);
        return i(tag,msgStr,tr);
    }

    public static int i(String tag,String msgTag,byte[] msg,Throwable tr){
        String msgStr = ByteUtils.bytesToHexString(msg);
        return i(tag,msgTag + ":\r\n" + msgStr,tr);
    }

    public static int w(String tag, String msg) {
        if(WARN_LEVER > lever){
            return Log.w(tag,msg);
        }
        return -1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if(WARN_LEVER > lever){
            return Log.w(tag,msg,tr);
        }
        return -1;
    }

    public static boolean isLoggable(String s, int i){
        return Log.isLoggable(s,i);
    }

    public static int w(String tag, Throwable tr) {
        if(WARN_LEVER > lever){
            return Log.w(tag,tr);
        }
        return -1;
    }

    public static int e(String tag, String msg) {
        if(ERROR_LEVER > lever){
            return Log.e(tag,msg);
        }
        return -1;
    }

    public static int e(String tag,Throwable tr){
        String message =  "ERROR" ;
        if(tr != null && tr.getMessage() != null){
            message = tr.getMessage();
        }
        return e(tag,message,tr);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if(ERROR_LEVER > lever){
            return Log.e(tag,msg,tr);
        }
        return -1;
    }

    public static int wtf(String tag, String msg) {
        if(ASSERT_LEVER > lever){
            return Log.wtf(tag,msg);
        }
        return -1;
    }

    public static int wtf(String tag, Throwable tr) {
        if(ASSERT_LEVER > lever){
            return Log.wtf(tag,tr);
        }
        return -1;
    }

    public static int wtf(String tag, String msg, Throwable tr) {
       if(ASSERT_LEVER > lever){
           return Log.wtf(tag,msg,tr);
       }
       return -1;
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }


    public static void stack(){
        Throwable throwable = new Throwable();
        // 需要处理TAG 要读出上面class method的信息，后续添上
        i("STACK",getStackTraceString(throwable));
    }

}
