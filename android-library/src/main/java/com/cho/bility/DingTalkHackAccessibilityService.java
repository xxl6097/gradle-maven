package com.cho.bility;

import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.RequiresApi;
import com.cho.bility.utils.BaseAccessibilityService;


public class DingTalkHackAccessibilityService extends BaseAccessibilityService {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        if (DingTalkClicker.getInstance().canDaking()) {
            if (DingTalkClicker.DINGTALK.equalsIgnoreCase(packageName)) {
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                DingTalkClicker.getInstance().onViewEvent(this,rootNode);
            }
        }

//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            if (DINGTALK.equalsIgnoreCase(packageName)) {
//                AccessibilityNodeInfo child = getRootInActiveWindow();
//                //proceed(child, child);
//                //test(child);
//            }
//        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//            if (DingTalkManager.getInstance().canWorking() || true) {
//                if (DINGTALK.equalsIgnoreCase(packageName)) {
//                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//                    DingTalkClicker.getInstance().onViewEvent(this,rootNode);
//                }
//            }else if (SETTING.equalsIgnoreCase(packageName)) {
//                forceCloseApp(event);
//            }
//        }

    }

}
