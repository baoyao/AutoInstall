package com.example.testhongbao;

import java.util.ArrayList;
import java.util.List;

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
    private final boolean DEBUG = false;
    private List<NodeInfo> mNodes = new ArrayList<NodeInfo>();
    private boolean isCommandSupport = true;//是否支持口令红包
    private boolean mCanOpen = false;

    private String TITLE_FIND_RED_PACKAGE, TITLE_CAN_OPEN, TITLE_OPENED,
            TITLE_OPENING, TITLE_SENDED, TITLE_ALREADY_OPENED, TITLE_MISS,
            TITLE_COMMAND_RED_PACKAGE, TITLE_CLICK_INPUT_COMMAND,
            TITLE_SEND_BUTTON;

    public QChatUtils(Context context) {
        this.mContext = context;
        if (DEBUG)
            Log.v("tt", "----qchat oncreate----");

        TITLE_FIND_RED_PACKAGE = mContext.getResources().getString(
                R.string.title_qchat_find_red_package);
        TITLE_CAN_OPEN = mContext.getResources().getString(
                R.string.title_qchat_can_open);
        TITLE_OPENED = mContext.getResources().getString(
                R.string.title_qchat_opened);
        TITLE_OPENING = mContext.getResources().getString(
                R.string.title_qchat_opening);
        TITLE_SENDED = mContext.getResources().getString(
                R.string.title_qchat_sended);
        TITLE_ALREADY_OPENED = mContext.getResources().getString(
                R.string.title_qchat_already_opened);
        TITLE_MISS = mContext.getResources().getString(
                R.string.title_qchat_miss);

        TITLE_COMMAND_RED_PACKAGE = mContext.getResources().getString(
                R.string.title_command_red_package);
        TITLE_CLICK_INPUT_COMMAND = mContext.getResources().getString(
                R.string.title_click_input_command);
        TITLE_SEND_BUTTON = mContext.getResources().getString(
                R.string.title_send_button);
        mCanOpen = false;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
        try {
            final int eventType = event.getEventType();
            if (DEBUG)
                Log.v("tt", "\n\n**qchat onAccessibilityEvent eventType: "
                        + eventType + " event " + event);

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                launchAppFromNotification(event);
            } else {
                isCommandSupport = PublicUtils.getConfiguration(mContext);
                normalPackageIndex = -1;
                CommandAlreadyOpenedIndex=-1;
                CommandNeedOpenIndex=-1;
                InputButtonIndex = -1;
                SendButtonIndex = -1;
                mNodes.clear();
                getAllNode(event.getSource(), "root");
                doNode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG)
                Log.v("tt", "onAccessibilityEvent Exception " + e);
        }
    }

    private void doClick(AccessibilityNodeInfo node, CharSequence tag) {

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        if (DEBUG)
            Log.v("tt", "doClick node [" + tag + "]");
    }
    
    
    private int normalPackageIndex = -1;//普通红包的位置
    private int CommandAlreadyOpenedIndex=-1;//已经领完的字符所在的位置
    private int CommandNeedOpenIndex=-1;//要打开的口令红包的位置
    private int InputButtonIndex = -1;//输入指令的位置
    private int SendButtonIndex = -1;//发送按钮的位置
    private AccessibilityNodeInfo mSendButton;
    private boolean isClickInputButton = false;

    private void doNode() {
        
        if(mNodes.size() == 0){
            return;
        }

        if (mCanOpen && normalPackageIndex != -1) {//打开普通红包
            doClick(mNodes.get(normalPackageIndex).parentNode, "opening normal package");
            mCanOpen = false;
            normalPackageIndex = -1;
        }

        if(mCanOpen&&CommandAlreadyOpenedIndex-CommandNeedOpenIndex<0&&CommandNeedOpenIndex!=-1){//打开口令红包
          doClick(mNodes.get(CommandNeedOpenIndex).parentNode, "click command package");
          CommandNeedOpenIndex=-1;
          mCanOpen = false;
        }

        if (InputButtonIndex!=-1&&!isClickInputButton) {//输入口令
            doClick(mNodes.get(InputButtonIndex).parentNode, "click input command");
            InputButtonIndex=-1;
            isClickInputButton = true;
        }
        
        if(SendButtonIndex!=-1){
            mSendButton=mNodes.get(SendButtonIndex).childNode;
        }
        
        if(isClickInputButton){//领取红包
            doClick(mSendButton, "click send button command");
        }

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
                Log.v("tt", "--" + str + " parentNode: " + parentNode.getText()
                        + " childNode: " + childNode.getText());

            CharSequence text = childNode.getText();
            if (text != null) {
                if (text.toString().contains(TITLE_CAN_OPEN)) {//普通红包
                    normalPackageIndex = mNodes.size();
                }

                // command support begin
                if (!isCommandSupport) {
                    continue;
                }
                if (text.toString().contains(TITLE_COMMAND_RED_PACKAGE)) {//口令红包
//                  doClick(parentNode, "click command package");
                    CommandNeedOpenIndex = mNodes.size();
                }
                if(text.toString().contains("口令红包")&&text.toString().contains("被领完")){//已被领
                    CommandAlreadyOpenedIndex = mNodes.size();
                }
                if (text.toString().contains(TITLE_CLICK_INPUT_COMMAND)) {//输入口令
//                  doClick(parentNode, "click input command");
                    InputButtonIndex = mNodes.size();
                }
                if (text.toString().contains(TITLE_SEND_BUTTON)) {//发送按钮
//                    doClick(childNode, "click send button");
                    SendButtonIndex = mNodes.size();
                }
                // command support end
                
                mNodes.add(new NodeInfo(parentNode, childNode,false, false));
            }
            if (childNode.getChildCount() > 0) {
                getAllNode(childNode, "child");
            }
        }
    }

    private void launchAppFromNotification(AccessibilityEvent event) {
        try {
            Notification notification = (Notification) event
                    .getParcelableData();
            if (PublicUtils.getNotificationContent(event).contains(
                    TITLE_FIND_RED_PACKAGE)) {
                PublicUtils.unlockScreen(mContext);
                notification.contentIntent.send();
                mCanOpen = true;
                isClickInputButton = false;
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
