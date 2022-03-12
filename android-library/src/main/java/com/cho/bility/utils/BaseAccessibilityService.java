package com.cho.bility.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.List;

public class BaseAccessibilityService extends AccessibilityService {

    private AccessibilityManager mAccessibilityManager;
    private Context mContext;
    private static BaseAccessibilityService mInstance;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static BaseAccessibilityService getInstance() {
        if (mInstance == null) {
            mInstance = new BaseAccessibilityService();
        }
        return mInstance;
    }

    public boolean foundView(AccessibilityNodeInfo node,String name){
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


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.alibaba.android.rimet"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);
    }


    public boolean isAccessibilitySettingsOn(Context mContext,Class clasz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + clasz.getCanonicalName();
        Log.i("uuuuuuu", "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("uuuuuuu", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("uuuuuuu", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            Log.v("uuuuuuu", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Log.v("uuuuuuu", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("uuuuuuu", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("uuuuuuu", "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    public boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            String name = info.getId();
            Log.e("uuuu","==>"+name);
            if (name.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 强制关闭APP
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void forceCloseApp1(AccessibilityEvent event){
        CharSequence className = event.getClassName();
        if (className.equals("com.android.settings.applications.InstalledAppDetailsTop")) {
            //AccessibilityNodeInfo info = findViewByText("强行停止");
            AccessibilityNodeInfo info = findViewByID("com.android.settings:id/right_button");
            if (info!=null&&info.isEnabled()) {
                performViewClick(info);
                if (className.equals("android.app.AlertDialog")) {
                    clickTextViewByText("确定");
                    performBackClick();
                }
            } else {
                performBackClick();
            }
        }
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
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


    public void slide(AccessibilityNodeInfo parent,int scroll_type){
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
    public void upslide(){
        slide(getRootInActiveWindow(),AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void downslide(){
        slide(getRootInActiveWindow(),AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }
    /**
     * 模拟返回操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void performBackClick() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    /**
     * 模拟下滑操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void performScrollBackward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟上滑操作
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void performScrollForward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AccessibilityNodeInfo findViewByText(String text) {
        return findViewByText(text, false);
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AccessibilityNodeInfo clickTextViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickTextViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
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

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void print(AccessibilityNodeInfo info){
        if (info==null)
            return;
        String rid = info.getViewIdResourceName();
        CharSequence text = info.getText();
        CharSequence name = info.getClassName();
        //Log.e("uu","-->"+text+" "+name+" "+rid);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean click(String target){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            performViewClick(info);
            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickParent(String target){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            info = info.getParent();
            if(info != null){
                print(info);
                performViewClick(info);
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickChild(String target,int index){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            info = info.getChild(index);
            if(info != null){
                print(info);
                performViewClick(info);
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickChild(String target,int index,int indexx){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            info = info.getChild(index);
            if(info != null){
                print(info);
                info = info.getChild(indexx);
                if(info != null){
                    print(info);
                    performViewClick(info);
                    return true;
                }
            }
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean input(String target,String text){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            Bundle argument = new Bundle();
            argument.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,argument);
            return true;
        }
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean swipeChild(String target,int index){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            info = info.getChild(index);
            if (info!=null) {
                print(info);
                info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean swipe(String target){
        AccessibilityNodeInfo info = findViewByID(target);
        if (info!=null) {
            print(info);
            info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            return true;
        }
        return false;
    }


    private  boolean isOpen=false;//防止开启后多次点击
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void scrollDeveloperCllick(String text, String listId, AccessibilityNodeInfo rootInActiveWindow) {
        if (rootInActiveWindow != null) {
            List<AccessibilityNodeInfo> item = rootInActiveWindow.findAccessibilityNodeInfosByText(text); //根据关键字查找某控件元素
            List<AccessibilityNodeInfo> list = rootInActiveWindow.findAccessibilityNodeInfosByViewId(listId); //根据resource id 查找容器元素；判断关键字查找出的元素是否在该容器元素中；
            if (item == null || item.size() == 0) { // 关键字元素不存在，则滚动容器元素
                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo parent = list.get(0).getParent().getParent();
                    if (parent!=null){
                        for (int i = 0; i < list.size(); i++) {
                            if (parent.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                                scrollDeveloperCllick(text, listId,rootInActiveWindow);
                                isOpen=true;
                            }
                            return;
                        }
                    }
                }

            } else {
                if (list!=null){
                    for (int i = 0; i < list.size(); i++) {
                        String s = list.get(i).getParent().getChild(0).getText().toString();
                        if (!TextUtils.isEmpty(s) && s.equals(text)&&isOpen){
                            list.get(i) .performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            isOpen=false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void closeApp(String appname){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", appname, null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }
    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean whileclick(AccessibilityNodeInfo child){
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

    /**
     * 模拟back按键
     */
    public void keyevent(int keycode) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + keycode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 模拟back按键
     */
    public void tap(int x,int y) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input tap " + x + " "+y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
