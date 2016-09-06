package com.example.testhongbao;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author houen.bao
 * @date Sep 6, 2016 4:24:09 PM
 */
public class NodeInfo {
    
    AccessibilityNodeInfo parentNode;
    AccessibilityNodeInfo childNode;
    boolean hasPackage;
    boolean isOpened;

    public NodeInfo() {
    }

    public NodeInfo(AccessibilityNodeInfo parentNode, AccessibilityNodeInfo childNode, boolean hasPackage,
            boolean isOpened) {
        super();
        this.parentNode = parentNode;
        this.childNode = childNode;
        this.hasPackage = hasPackage;
        this.isOpened = isOpened;
    }
}
