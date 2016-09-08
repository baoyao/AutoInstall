package com.example.testhongbao;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author houen.bao
 * @date Sep 6, 2016 2:46:21 PM
 */
public class MChatUtils {

    private Context mContext;

    public MChatUtils(Context context) {
        this.mContext = context;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
        try {
            final int eventType = event.getEventType();
            if (eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                Log.v("tt","\n\n\n");
                getAllNode(event.getSource(), "root",0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tt", "onAccessibilityEvent Exception " + e);
        }
    }

    
    @SuppressLint("NewApi")
    private void getAllNode(AccessibilityNodeInfo parentNode, String str,int count) {
        if (parentNode == null) {
            return;
        }
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = parentNode.getChild(i);
            if (childNode == null) {
                return;
            }
            Log.v("tt", "-"+count+"-" + str + " parentNode: " + parentNode.getText() + " childNode: " + childNode.getText());

            CharSequence text = childNode.getText();
            if (text != null) {

            }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child",count+1);
            }
        }
    }

    public void onDestroy() {
        Log.v("tt", "----mchat onDestroy----");
    }

}
