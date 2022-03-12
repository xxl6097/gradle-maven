package com.cho.bility.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.List;

public class AbilityUtil {

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public static void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo findViewByID(AccessibilityService accessibilityService, String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickTextViewByID(AccessibilityService service, String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    return true;
                }
            }
        }
        return false;
    }


    public static void slide(AccessibilityNodeInfo parent,int scroll_type){
        if (parent==null)
            return ;
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            boolean sucess = parent.performAction(scroll_type);
            if (sucess){
                return ;
            }
            if (parent.getChild(i) != null) {
                slide(parent.getChild(i),scroll_type);
            }
        }
        return ;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void upslide(AccessibilityService accessibilityService){
        slide(accessibilityService.getRootInActiveWindow(),AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void downslide(AccessibilityService accessibilityService){
        slide(accessibilityService.getRootInActiveWindow(),AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static AccessibilityNodeInfo clickTextViewByText(AccessibilityService accessibilityService,String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 模拟返回操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void performBackClick(AccessibilityService accessibilityService) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    /**
     * 模拟返回操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void performBack(AccessibilityService accessibilityService) {
        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    /**
     * 模拟返回操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void performHome(AccessibilityService accessibilityService) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public static boolean whileclick(AccessibilityNodeInfo child){
        AccessibilityNodeInfo parent = child;
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickChild(AccessibilityService accessibilityService,String target,int index){
        AccessibilityNodeInfo info = findViewByID(accessibilityService,target);
        if (info!=null) {
            info = info.getChild(index);
            if(info != null){
                performViewClick(info);
                return true;
            }
        }
        return false;
    }

    public static boolean foundView(AccessibilityNodeInfo node, String name){
        if (node == null)
            return false;
        if (name == null)
            return false;
        CharSequence viewName = node.getText();
        if (viewName == null)
            return false;
        if (viewName.toString().equalsIgnoreCase(name))
            return true;
        return false;
    }

    public static boolean containView(AccessibilityNodeInfo node, String name){
        if (node == null)
            return false;
        if (name == null)
            return false;
        CharSequence viewName = node.getText();
        if (viewName == null)
            return false;
        if (viewName.toString().contains(name))
            return true;
        return false;
    }

    public static boolean foundClassName(AccessibilityNodeInfo node, String name){
        if (node == null)
            return false;
        if (name == null)
            return false;
        CharSequence claszname = node.getClassName();
        if (claszname == null)
            return false;
        if (claszname.toString().equalsIgnoreCase(name))
            return true;
        return false;
    }
    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public static void inputText(Context context,AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getViewByID(AccessibilityService accessibilityService,String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }
}
