package com.example.testhongbao;

import java.util.ArrayList;
import java.util.List;

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
    private List<NodeInfo> mNodes = new ArrayList<NodeInfo>();
    private boolean mCanOpen = false;

    private String TITLE_FIND_RED_PACKAGE, TITLE_CAN_OPEN, TITLE_OPENED, TITLE_OPENING, TITLE_SENDED,
            TITLE_ALREADY_OPENED, TITLE_MISS;

    public QChatUtils(Context context) {
        // TODO Auto-generated constructor stub
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
                launcherAppFromNotification(event);
            } else {
                mNodes.clear();
                getAllNode(event.getSource(), "root");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tt", "onAccessibilityEvent Exception " + e);
        }
    }

    private void doClick(AccessibilityNodeInfo node, CharSequence title) {

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        if (DEBUG)
            Log.v("tt", "doClick node [" + title + "]");
    }

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
            Log.v("tt", "--" + str + " parentNode: " + parentNode+ " childNode: " + childNode);
            CharSequence text = childNode.getText();
            if (text != null) {
                NodeInfo node = new NodeInfo(parentNode, childNode, false, false);
                if (text.toString().contains(TITLE_CAN_OPEN)) {
                    node.hasPackage = true;
                    // CAN_OPEN_INDEX = mNodes.size();
                    if(mCanOpen){
                        doClick(parentNode, "opening");
                        mCanOpen=false;
                        return;
                    }
                }
                // mNodes.add(node);
                if (text.toString().contains(TITLE_OPENING)) {
                    // doClick(childNode, text);
                    // mCanOpen = false;
                }

                if (text.toString().contains(TITLE_ALREADY_OPENED)) {
                    // mCanOpen = false;
                }

                if (text.toString().contains(TITLE_SENDED)) {
                    // hasSendedTitle = true;
                }
            }
            // else if (hasSendedTitle) {
            // Packages p = new Packages(parentNode, childNode, false, false);
            // mNodes.add(p);
            // }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child");
            }
        }
    }

    private void launcherAppFromNotification(AccessibilityEvent event) {
        try {
            Notification notification = (Notification) event.getParcelableData();
            if (PublicUtils.getNotificationContent(event).contains(TITLE_FIND_RED_PACKAGE)) {
                PublicUtils.unlockScreen(mContext);
                notification.contentIntent.send();
                mCanOpen = true;
                if (DEBUG)
                    Log.v("tt", "launcher app");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "launcher app Exception " + e);
        }
    }

    public void onDestroy() {
        if (DEBUG)
            Log.v("tt", "----qchat onDestroy----");

    }

}
