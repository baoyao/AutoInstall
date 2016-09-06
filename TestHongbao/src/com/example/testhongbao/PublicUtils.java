package com.example.testhongbao;

import java.lang.reflect.Field;
import java.util.List;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author houen.bao
 * @date Sep 6, 2016 3:15:25 PM
 */
public class PublicUtils {

    private static KeyguardManager.KeyguardLock mKeyguardLock;
    
    public static void unlockScreen(Context context) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
            wakeLock.acquire(5 * 1000);

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            mKeyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
            mKeyguardLock.disableKeyguard();
        } catch (Exception e) {
            e.printStackTrace();
                Log.v("tt", "unlockScreen Exception " + e);
        }
    }
    
    public static void reenableKeyguard(){
        if(mKeyguardLock!=null){
            mKeyguardLock.reenableKeyguard();
        }
    }
    

    public static String getNotificationContent(AccessibilityEvent event) throws Exception {
        Field f = event.getClass().getSuperclass().getDeclaredField("mText");
        f.setAccessible(true);
        List<CharSequence> texts = (List<CharSequence>) f.get(event);
        String text = "";
        if (texts != null) {
            for (CharSequence str : texts) {
                text += str;
            }
        }
        Log.v("tt", "getNotificationContent text: " + text);
        return text;
    }
}
