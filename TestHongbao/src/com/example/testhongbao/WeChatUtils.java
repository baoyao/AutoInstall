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
 * @date Sep 1, 2016 2:38:24 PM
 */
public class WeChatUtils {

    private Context mContext;
    private List<NodeInfo> mNodes = new ArrayList<NodeInfo>();
    private String TITLE_FIND_RED_PACKAGE, TITLE_CAN_OPEN, TITLE_OPENED, TITLE_OPENING, TITLE_SENDED,
            TITLE_ALREADY_OPENED, TITLE_MISS;
    private boolean mCanOpen = false;
    private final boolean DEBUG = false;

    public WeChatUtils(Context context) {
        this.mContext = context;
        if (DEBUG)
            Log.v("tt", "----WeChat oncreate----");

        TITLE_FIND_RED_PACKAGE = mContext.getResources().getString(R.string.title_wechat_find_red_package);
        TITLE_CAN_OPEN = mContext.getResources().getString(R.string.title_wechat_can_open);
        TITLE_OPENED = mContext.getResources().getString(R.string.title_wechat_opened);
        TITLE_OPENING = mContext.getResources().getString(R.string.title_wechat_opening);
        TITLE_SENDED = mContext.getResources().getString(R.string.title_wechat_sended);
        TITLE_ALREADY_OPENED = mContext.getResources().getString(R.string.title_wechat_already_opened);
        TITLE_MISS = mContext.getResources().getString(R.string.title_wechat_miss);
        mCanOpen = false;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            final int eventType = event.getEventType();
            if (DEBUG)
                Log.v("tt", "\n\n**wechat onAccessibilityEvent eventType: " + eventType + " mCanOpen: " + mCanOpen);

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                launchAppFromNotification(event);
            } else {
                mNodes.clear();
                CAN_OPEN_INDEX = -1;
                OPENED_INDEX = -1;
                hasSendedTitle = false;
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
            Log.v("tt", "-------doNodes mNodes.size() " + mNodes.size() + " | " + CAN_OPEN_INDEX + " | "
                    + OPENED_INDEX);
        if (mNodes.size() == 0) {
            return;
        }

        if (OPENED_INDEX < CAN_OPEN_INDEX && mCanOpen) {
            doClick(mNodes.get(CAN_OPEN_INDEX).parentNode, mNodes.get(CAN_OPEN_INDEX).childNode.getText());
            mNodes.clear();
        }

        for (int i = 0; i < mNodes.size(); i++) {
            NodeInfo p = mNodes.get(i);
            if (p.childNode.toString().contains(TITLE_SENDED)) {
                int index = i + 2;
                doClick(mNodes.get(index).childNode, "opening");
                mCanOpen = false;
            }
            if (p.childNode.toString().contains(TITLE_MISS)) {
                doClick(mNodes.get(i).parentNode, "miss");
                mCanOpen = false;
            }
        }
    }

    private int CAN_OPEN_INDEX = -1;
    private int OPENED_INDEX = -1;
    private boolean hasSendedTitle = false;

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
                NodeInfo node = new NodeInfo(parentNode, childNode, false, false);
                if (text.toString().contains(TITLE_CAN_OPEN)) {
                    node.hasPackage = true;
                    CAN_OPEN_INDEX = mNodes.size();
                } else if (text.toString().contains(TITLE_OPENED)) {
                    node.isOpened = true;
                    OPENED_INDEX = mNodes.size();
                }
                mNodes.add(node);
                if (text.toString().contains(TITLE_OPENING)) {
                    doClick(childNode, text);
                    mCanOpen = false;
                }

                if (text.toString().contains(TITLE_ALREADY_OPENED)) {
                    mCanOpen = false;
                }

                if (text.toString().contains(TITLE_SENDED)) {
                    hasSendedTitle = true;
                }
            } else if (hasSendedTitle) {
                NodeInfo p = new NodeInfo(parentNode, childNode, false, false);
                mNodes.add(p);
            }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child");
            }
        }
    }

    private void launchAppFromNotification(AccessibilityEvent event) {
        try {
            Notification notification = (Notification) event.getParcelableData();
            if (needLaunchApp(event)) {
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

    private boolean needLaunchApp(AccessibilityEvent event) throws Exception {
        if (PublicUtils.getNotificationContent(event).contains(TITLE_FIND_RED_PACKAGE)) {
            return true;
        }
        return false;
    }

    public void onDestroy() {
        if (DEBUG)
            Log.v("tt", "----WeChat onDestroy----");
    }

}
