package com.example.testhongbao;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author houen.bao
 * @date Aug 31, 2016 2:19:17 PM
 */
public class MonitorService extends AccessibilityService {

    private WeChatUtils mWeChatUtils;
    private QChatUtils mQChatUtils;
    private final String WECHAT_PACKAGE = "com.tencent.mm", QCHAT_PACKAGE = "com.tencent.mobileqq";

    @Override
    public void onCreate() {
        super.onCreate();
        mWeChatUtils = new WeChatUtils(this);
        mQChatUtils = new QChatUtils(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName=event.getPackageName().toString();
        if(WECHAT_PACKAGE.equals(packageName)){
            mWeChatUtils.onAccessibilityEvent(event);
        }else if(QCHAT_PACKAGE.equals(packageName)){
            mQChatUtils.onAccessibilityEvent(event);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mWeChatUtils.onDestroy();
        mQChatUtils.onDestroy();
        super.onDestroy();
    }

}