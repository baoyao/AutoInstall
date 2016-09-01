package com.example.testhongbao;

import java.lang.reflect.Field;
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
public class MyUtils {

    private Context mContext;
    private List<AccessibilityNodeInfo> lingNodes = new ArrayList<AccessibilityNodeInfo>();
    private final String HONG = "[微信红包]";
    private final String LING = "领取红包";
    private final String CAI = "拆红包";

    public MyUtils(Context context) {
        this.mContext = context;
        Log.v("tt", "----oncreate----");
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            final int eventType = event.getEventType();
            Log.v("tt", "\n\n**onAccessibilityEvent eventType: " + eventType);
            // Log.v("tt", "event.getParcelableData(): : " +
            // event.getParcelableData());
            // Log.v("tt", "event: " + event);

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                launcherAppFromNotification(event);
            } else {
                lingNodes.clear();
                AccessibilityNodeInfo rootNode = event.getSource();
                getAllNode(rootNode, "root");
                doNodes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tt", "onAccessibilityEvent Exception: " + e);
        }
    }

    private void doClick(AccessibilityNodeInfo node) {

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        // node.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);

        Log.v("tt", "doClick node [" + node.getText() + "]");
    }

    private void doNodes() {
        Log.v("tt", "-------doNodes lingNodes.size() " + lingNodes.size());

        if (lingNodes.size() > 0) {
            doClick(lingNodes.get(0));
            lingNodes.clear();
        }
    }

    private void getAllNode(AccessibilityNodeInfo rootNode, String str) {
        if (rootNode == null) {
            return;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = rootNode.getChild(i);
            if (childNode == null) {
                return;
            }
            Log.v("tt", "--" + str + " rootNode: " + rootNode.getText() + " childNode: " + childNode.getText());
            CharSequence text = childNode.getText();
            if (text != null) {
                if (text.toString().contains(LING)) {
                    lingNodes.clear();
                    AccessibilityNodeInfo lingNode = rootNode;
                    if (lingNode != null) {
                        lingNodes.add(lingNode);
                    }
                }
                if (text.toString().contains("Open")) {
                     doClick(childNode);
                }
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
                notification.contentIntent.send();
                Log.v("tt", "launcher app");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tt", "launcher app Exception " + e);
        }
    }

    private boolean needLauncherApp(AccessibilityEvent event) throws Exception {
        if (getNorificationContent(event).contains(HONG)) {
            return true;
        }
        return false;
    }

    private String getNorificationContent(AccessibilityEvent event) throws Exception {
        Field f = event.getClass().getSuperclass().getDeclaredField("mText");
        f.setAccessible(true);
        List<CharSequence> texts = (List<CharSequence>) f.get(event);
        String text = "";
        if (texts != null) {
            for (CharSequence str : texts) {
                text += str;
            }
        }
        Log.v("tt", "getNorificationContent text: " + text);
        return text;
    }

}
