package com.example.testhongbao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author houen.bao
 * @date Sep 1, 2016 2:38:24 PM
 */
public class MyUtils {

    private Context mContext;
    private List<Packages> lingNodes = new ArrayList<Packages>();
    private String HONG = "";
    private String LING = "";
    private String YILING = "";
    private String CAI = "";
    private String FA = "";
    private String CUN = "";
    private String KANKAN="";

    private int stepCount = 0;

    private final boolean DEBUG = false;

    public MyUtils(Context context) {
        this.mContext = context;
        if (DEBUG)
            Log.v("tt", "----oncreate----");

        HONG = mContext.getResources().getString(R.string.hong);
        LING = mContext.getResources().getString(R.string.ling);
        YILING = mContext.getResources().getString(R.string.yi_ling);
        CAI = mContext.getResources().getString(R.string.cai);
        FA = mContext.getResources().getString(R.string.fa);
        CUN = mContext.getResources().getString(R.string.cun);
        KANKAN = mContext.getResources().getString(R.string.kankan);
        stepCount = 0;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            final int eventType = event.getEventType();
            if (DEBUG)
                Log.v("tt", "\n\n**onAccessibilityEvent eventType: " + eventType+" stepCount: "+stepCount);

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                launcherAppFromNotification(event);
            } else if(stepCount != 1){
                lingNodes.clear();
                LING_INDEX = -1;
                YILING_INDEX = -1;
                hasFa = false;
                AccessibilityNodeInfo parentNode = event.getSource();
                getAllNode(parentNode, "parent");
                doNodes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "onAccessibilityEvent Exception: " + e);
        }
    }
    
    private void doClick(AccessibilityNodeInfo node, CharSequence title) {

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        if (DEBUG)
            Log.v("tt", "doClick node [" + title + "]");
    }

    private void doNodes() {
        if (DEBUG)
            Log.v("tt", "-------doNodes lingNodes.size() " + lingNodes.size() + " | " + LING_INDEX + " | "
                    + YILING_INDEX);
        if (lingNodes.size() > 0 && YILING_INDEX < LING_INDEX) {
            doClick(lingNodes.get(LING_INDEX).parentNode, lingNodes.get(LING_INDEX).childNode.getText());
            lingNodes.clear();
        }
        
        for(int i = 0;i<lingNodes.size();i++){
            Packages p=lingNodes.get(i);
            if(p.childNode.toString().contains(FA)){
                int index = i+2;
                doClick(lingNodes.get(index).childNode,"kai");
                stepCount = 1;
            }
            if(p.childNode.toString().contains(KANKAN)){
                doClick(lingNodes.get(i).parentNode,"kankan");
                stepCount = 1;
            }
        }
    }

    private int LING_INDEX = -1;
    private int YILING_INDEX = -1;
    private boolean hasFa=false;

    private void getAllNode(AccessibilityNodeInfo parentNode, String str) {
        if (parentNode == null) {
            return;
        }
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = parentNode.getChild(i);
            if (childNode == null) {
                return;
            }
            if (DEBUG)
                Log.v("tt", "--" + str + " parentNode: " + parentNode.getText() + " childNode: " + childNode.getText());
            CharSequence text = childNode.getText();
            if (text != null) {
                Packages p = new Packages(parentNode, childNode, false, false);
                if (text.toString().contains(LING)) {
                    p.hasPackage = true;
                    LING_INDEX = lingNodes.size();
                } else if (text.toString().contains(YILING)) {
                    p.isOpened = true;
                    YILING_INDEX = lingNodes.size();
                }
                lingNodes.add(p);
                if (text.toString().contains(CAI)) {
                    doClick(childNode, text);
                    stepCount = 1;
                }
                
                if(text.toString().contains(CUN)){
                    stepCount = 1;
                }
                
                if(text.toString().contains(FA)){
                    hasFa=true;
                }
            }else if(hasFa){
                Packages p = new Packages(parentNode, childNode, false, false);
                lingNodes.add(p);
            }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child");
            }
        }
    }

    private void launcherAppFromNotification(AccessibilityEvent event) {
        try {
            Notification notification = (Notification) event.getParcelableData();
            if (needLauncherApp(event)) {
                unlockScreen();
                notification.contentIntent.send();
                stepCount = 0;
                if (DEBUG)
                    Log.v("tt", "launcher app");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "launcher app Exception " + e);
        }
    }

    private boolean needLauncherApp(AccessibilityEvent event) throws Exception {
        if (getNotificationContent(event).contains(HONG)) {
            return true;
        }
        return false;
    }

    private String getNotificationContent(AccessibilityEvent event) throws Exception {
        Field f = event.getClass().getSuperclass().getDeclaredField("mText");
        f.setAccessible(true);
        List<CharSequence> texts = (List<CharSequence>) f.get(event);
        String text = "";
        if (texts != null) {
            for (CharSequence str : texts) {
                text += str;
            }
        }
        if (DEBUG)
            Log.v("tt", "getNotificationContent text: " + text);
        return text;
    }

    private void unlockScreen() {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
            keyguardLock.disableKeyguard();

            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

            wakeLock.acquire(5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "unlockScreen Exception " + e);
        }
    }

    class Packages {
        AccessibilityNodeInfo parentNode;
        AccessibilityNodeInfo childNode;
        boolean hasPackage;
        boolean isOpened;

        public Packages() {
        }

        public Packages(AccessibilityNodeInfo parentNode, AccessibilityNodeInfo childNode, boolean hasPackage,
                boolean isOpened) {
            super();
            this.parentNode = parentNode;
            this.childNode = childNode;
            this.hasPackage = hasPackage;
            this.isOpened = isOpened;
        }
    }

}
