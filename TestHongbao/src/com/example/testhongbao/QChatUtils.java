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
public class QChatUtils {

    private Context mContext;
    private final boolean DEBUG = true;
    private boolean mCanOpen = false;

    private String TITLE_FIND_RED_PACKAGE, TITLE_CAN_OPEN, TITLE_OPENED, TITLE_OPENING, TITLE_SENDED,
            TITLE_ALREADY_OPENED, TITLE_MISS;

    public QChatUtils(Context context) {
        this.mContext = context;
        if (DEBUG)
            Log.v("tt", "----qchat oncreate----");

        TITLE_FIND_RED_PACKAGE = mContext.getResources().getString(R.string.title_qchat_find_red_package);
        TITLE_CAN_OPEN = mContext.getResources().getString(R.string.title_qchat_can_open);
        TITLE_OPENED = mContext.getResources().getString(R.string.title_qchat_opened);
        TITLE_OPENING = mContext.getResources().getString(R.string.title_qchat_opening);
        TITLE_SENDED = mContext.getResources().getString(R.string.title_qchat_sended);
        TITLE_ALREADY_OPENED = mContext.getResources().getString(R.string.title_qchat_already_opened);
        TITLE_MISS = mContext.getResources().getString(R.string.title_qchat_miss);
        mCanOpen = false;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
        try {
            final int eventType = event.getEventType();
            if (DEBUG)
                Log.v("tt", "\n\n**qchat onAccessibilityEvent eventType: " + eventType + " event " + event);

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                launchAppFromNotification(event);
            } else {
                getAllNode(event.getSource(), "root");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tt", "onAccessibilityEvent Exception " + e);
        }
    }

    private void doClick(AccessibilityNodeInfo node, CharSequence tag) {

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        if (DEBUG)
            Log.v("tt", "doClick node [" + tag + "]");
    }

    @SuppressLint("NewApi")
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
                if (text.toString().contains(TITLE_CAN_OPEN)) {
                    if (mCanOpen) {
                        doClick(parentNode, "opening");
                        mCanOpen = false;
                        return;
                    }
                }
            }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child");
            }
        }
    }

    private void launchAppFromNotification(AccessibilityEvent event) {
        try {
            Notification notification = (Notification) event.getParcelableData();
            if (PublicUtils.getNotificationContent(event).contains(TITLE_FIND_RED_PACKAGE)) {
                PublicUtils.unlockScreen(mContext);
                notification.contentIntent.send();
                mCanOpen = true;
                if (DEBUG)
                    Log.v("tt", "launch app");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "launch app Exception " + e);
        }
    }

    public void onDestroy() {
        if (DEBUG)
            Log.v("tt", "----qchat onDestroy----");

    }

}
