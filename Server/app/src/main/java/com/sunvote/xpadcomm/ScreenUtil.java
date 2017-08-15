package com.sunvote.xpadcomm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManager;

public class ScreenUtil {
    static String TAG = "ScreenUtil";

    public static void setDarkMode(Activity context){
        try {
            if(isAutoBrightness(context)){
                ScreenUtil.stopAutoBrightness(context);
            }
            int  bval =	ScreenUtil.getScreenBrightness(context);
            Log.d(TAG, "brightness:"+bval);
            ScreenUtil.setBrightness(context, 0);
            ScreenUtil.saveBrightness(context, 0);
            bval =	ScreenUtil.getScreenBrightness(context);
            Log.d(TAG, "brightness:"+bval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNormalMode(Activity context , int level){
        try {
            if(isAutoBrightness(context)){
                ScreenUtil.stopAutoBrightness(context);
            }
            int  bval =	ScreenUtil.getScreenBrightness(context);
            Log.d(TAG, "brightness:"+bval);
            ScreenUtil.setBrightness(context, level);
            ScreenUtil.saveBrightness(context, level);
            bval =	ScreenUtil.getScreenBrightness(context);
            Log.d(TAG, "brightness:"+bval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param activity
     * @return
     */
    public static boolean isAutoBrightness(Activity activity) {
        boolean isAutoAdjustBright = false;
        try {
            isAutoAdjustBright = Settings.System.getInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isAutoAdjustBright;
    }

    /**
     * 获取屏幕的亮度
     *
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int brightnessValue = 0;
        try {
            brightnessValue = android.provider.Settings.System.getInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brightnessValue;
    }

    /**
     * 设置屏幕亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * 255f / 100 ;
        activity.getWindow().setAttributes(lp);


    }

    /**
     * 关闭亮度自动调节
     *
     * @param activity
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节
     *
     * @param activity
     */

    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     *
     * @param activity
     * @param brightness
     */
    public static void saveBrightness(Activity activity, int brightness) {
        Uri uri = android.provider.Settings.System
                .getUriFor("screen_brightness");
        ContentResolver resolver = activity.getContentResolver();
        int value =(int) (Float.valueOf(brightness) * 255f / 100) ;
        android.provider.Settings.System.putInt(resolver, "screen_brightness",  value);

        resolver.notifyChange(uri, null);
    }


    public static void screenPowerOff(Activity activity){
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        wakeLock.acquire();
        //做我们的工作，在这个阶段，我们的屏幕会持续点亮
        //释放锁，屏幕熄灭。
        wakeLock.release();
    }

}
