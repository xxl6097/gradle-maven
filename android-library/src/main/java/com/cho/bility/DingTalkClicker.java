package com.cho.bility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cho.bility.utils.AbilityUtil;
import com.cho.bility.utils.BaseAccessibilityService;
import com.cho.bility.utils.Logc;
import com.cho.bility.utils.PUtil;

public class DingTalkClicker {
    private static DingTalkClicker instance = null;

    public static String SETTING = "com.android.settings";
    public static String DINGTALK = "com.alibaba.android.rimet";
    public static String LOGIN_BTN_ID = "com.alibaba.android.rimet:id/tv";
    public static String BOTTOMTAB_BTN_ID = "com.alibaba.android.rimet:id/home_app_recycler_view";
    public static String BOTTOMTAB_DAKING_ID = "com.alibaba.android.rimet:id/h5_tabbaritem_txticon";
    public static String DAKA = "com.alibaba.android.rimet:id/ll_item_layout";
    private DingTalkEnum dingStep = DingTalkEnum.WORKBAR;
    private int click_count = 0;
    private boolean exception_exit = false;
    private String password = "2475431305";
    private OnDingClicker onDingClicker;
    private Runnable runnableKaoQin,runnableSuc;

    public static DingTalkClicker getInstance() {
        if (instance == null){
            synchronized (DingTalkClicker.class){
                if (null == instance){
                    instance = new DingTalkClicker();
                }
            }
        }
        return instance;
    }

    public void setOnDingClicker(OnDingClicker onDingClicker) {
        this.onDingClicker = onDingClicker;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        if (onDingClicker!=null){
            return onDingClicker.getPassword();
        }
        return password;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onViewEvent(final AccessibilityService service, final AccessibilityNodeInfo node){
        if (AbilityUtil.foundView(node,"登录")){
            if (dingStep == DingTalkEnum.WORKBAR) {
                login(service, service.getRootInActiveWindow());
                AbilityUtil.clickTextViewByID(service, LOGIN_BTN_ID);
                dingStep = DingTalkEnum.WORKBAR;
                logger(node.getText().toString());
            }
        }else if(AbilityUtil.foundView(node,"工作台")){
            if (dingStep == DingTalkEnum.WORKBAR){
                AbilityUtil.clickChild(service,BOTTOMTAB_BTN_ID, 2);
                dingStep = DingTalkEnum.KAOQINDAKA;
                logger(node.getText().toString());
            }
        }else if(AbilityUtil.foundView(node,"考勤打卡")){
            if (dingStep == DingTalkEnum.KAOQINDAKA){
                AbilityUtil.whileclick(node);
                dingStep = DingTalkEnum.CANDAKA;
                logger(node.getText().toString());
                runnableKaoQin = new Runnable() {
                    @Override
                    public void run() {
                        if (dingStep == DingTalkEnum.CANDAKA){
                            click_count++;
                            logger(node.getText().toString() + " 重复点击");
                            if (click_count >= 5){
                                click_count = 0;
                                dingStep = DingTalkEnum.MINE;
                                logger(node.getText().toString() + " 累计达到 " + click_count + "次，准备退出登录");
                                exception_exit = true;
                            }else{
                                dingStep = DingTalkEnum.KAOQINDAKA;
                            }
                            AbilityUtil.performBackClick(service);
                        }
                    }
                };
                new Handler().postDelayed(runnableKaoQin,30 * 1000);
            }
        }else if(AbilityUtil.foundView(node,"下班打卡") || AbilityUtil.foundView(node,"上班打卡")){
            //if (dingStep == DingTalkEnum.CANDAKA){}
            if (isDaKingTime()) {
                AbilityUtil.performViewClick(node);
                dingStep = DingTalkEnum.DAKASUCESS;
                logger(node.getText().toString());
            } else {
                if (onDingClicker!=null){
                    onDingClicker.daKingSucessUI();
                }
                dingStep = DingTalkEnum.MINE;
                AbilityUtil.performBackClick(service);
                logger(node.getText().toString() + "[非打卡时间，未打卡]");
            }

            if (runnableKaoQin!=null){
                new Handler().removeCallbacks(runnableKaoQin);
                runnableKaoQin = null;
            }
        }
        else if(AbilityUtil.foundView(node,"无法打卡")){
            if (dingStep == DingTalkEnum.CANDAKA){
                AbilityUtil.whileclick(node);
                AbilityUtil.performBackClick(service);
                dingStep = DingTalkEnum.MINE;
                logger(node.getText().toString());
            }
        }else if(AbilityUtil.containView(node,"打卡成功")){
            if (dingStep == DingTalkEnum.DAKASUCESS){
                if (onDingClicker!=null){
                    onDingClicker.daKingSucessUI();
                }
                AbilityUtil.performBackClick(service);
                dingStep = DingTalkEnum.MINE;
                logger(node.getText().toString());
                runnableSuc = new Runnable() {
                    @Override
                    public void run() {
                        if (dingStep == DingTalkEnum.MINE){
                            AbilityUtil.performBackClick(service);
                        }
                    }
                };
                new Handler().postDelayed(runnableSuc,30 * 1000);
            }
        }else if(AbilityUtil.foundView(node,"我的")){
            if (dingStep == DingTalkEnum.MINE){
                if (runnableSuc != null){
                    new Handler().removeCallbacks(runnableSuc);
                    runnableSuc = null;
                }
                logger(node.getText().toString());
                AbilityUtil.clickChild(service,BOTTOMTAB_BTN_ID, 4);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AbilityUtil.upslide(service);
                dingStep = DingTalkEnum.SETTING;
            }
        }else if(AbilityUtil.foundView(node,"设置")){
            if (dingStep == DingTalkEnum.SETTING){
                logger(node.getText().toString());
                AbilityUtil.clickTextViewByText(service,node.getText().toString());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AbilityUtil.upslide(service);
                dingStep = DingTalkEnum.LOGINOUT;
            }
        }else if(AbilityUtil.foundView(node,"退出登录")){
            if (dingStep == DingTalkEnum.LOGINOUT){
                logger(node.getText().toString());
                AbilityUtil.clickTextViewByText(service,node.getText().toString());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AbilityUtil.upslide(service);
                dingStep = DingTalkEnum.CONFIRE;
            }
        }else if(AbilityUtil.foundView(node,"确认")){
            AbilityUtil.clickTextViewByText(service,node.getText().toString());
            logger(node.getText().toString());
            if (dingStep == DingTalkEnum.CONFIRE && !exception_exit){
//                PUtil.forceStopApp(service,DINGTALK);
//                AbilityUtil.performHome(service);
                AbilityUtil.performBackClick(service);
                AbilityUtil.performBackClick(service);
                AbilityUtil.performBackClick(service);
                AbilityUtil.performBackClick(service);
                AbilityUtil.performBackClick(service);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dingStep = DingTalkEnum.WORKBAR;
                if (onDingClicker!=null){
                    onDingClicker.onFinish();
                }
            }
            exception_exit = false;
        }else if(AbilityUtil.foundView(node,"暂不更新")){//android:id/button2
            //更新 android:id/button1
            AbilityUtil.clickTextViewByText(service,node.getText().toString());
            logger(node.getText().toString());
        }else{
            if (node != null){
                int count = node.getChildCount();
                for (int i = 0; i < count; i++) {
                    if (node.getChild(i) != null) {
                        onViewEvent(service,node.getChild(i));
                    }
                }
            }
        }
    }

    private void login(AccessibilityService service,AccessibilityNodeInfo node){
        if (AbilityUtil.foundView(node,"请输入密码")) {
            CharSequence viewName = node.getText();
            logger(viewName.toString());
            AbilityUtil.inputText(service,node, getPassword());
        } else if (AbilityUtil.foundClassName(node,"android.widget.CheckBox") ) {
            AccessibilityNodeInfo parent = node;
            while (parent != null) {
                if (parent.isCheckable()) {
                    if (!parent.isChecked()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        logger("点击圈圈");
                        break;
                    }
                }
                parent = parent.getParent();
            }
        } else {
            if (node != null){
                for (int i = 0; i < node.getChildCount(); i++) {
                    if (node.getChild(i) != null) {
                        login(service,node.getChild(i));
                    }
                }
            }
        }
    }

    public void logger(String text){
        Logc.e(text);
        if (onDingClicker!=null){
            onDingClicker.logger(text);
        }
    }

    private boolean isDaKingTime() {
        if (onDingClicker!=null){
            return onDingClicker.isDaKingTime();
        }
        return PUtil.isDakingTime();
    }

    public boolean canDaking() {
        if (onDingClicker!=null){
            return onDingClicker.canDaking();
        }
        return true;
    }

    public void check(Context context){
        if (context == null)
            return;
        if (!BaseAccessibilityService.getInstance().isAccessibilitySettingsOn(context, DingTalkHackAccessibilityService.class)){
            Toast.makeText(context, "请开启无障碍服务！", Toast.LENGTH_SHORT).show();
            BaseAccessibilityService.getInstance().goAccess(context);
        }
    }

    public interface OnDingClicker{
        void logger(String text);
        String getPassword();
        boolean isDaKingTime();
        void daKingSucessUI();
        boolean canDaking();
        void onFinish();
    }
}
