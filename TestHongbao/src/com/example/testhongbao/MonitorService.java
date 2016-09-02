package com.example.testhongbao;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author houen.bao
 * @date Aug 31, 2016 2:19:17 PM
 */
public class MonitorService extends AccessibilityService {
    
    private MyUtils utils;
    
    @Override
    public void onCreate() {
        super.onCreate();
        utils = new MyUtils(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        utils.onAccessibilityEvent(event);
    }

    @Override
    public void onInterrupt() {

    }
}