package com.cho.bility.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PUtil {
    public static void forceStopApp(Context context,String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : listOfProcesses) {
            if (process.processName.contains(packageName)) {
                Log.e("xxxxx", process.processName + " : " + process.pid);
                android.os.Process.killProcess(process.pid);
                android.os.Process.sendSignal(process.pid, android.os.Process.SIGNAL_KILL);
                manager.killBackgroundProcesses(process.processName);
                break;

            }
        }
    }

    public static Calendar getBeijingTime() {
        Date date = new Date();  // 对应的北京时间是2017-08-24 11:17:10
        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
        bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
        bjSdf.format(date);
        Calendar now = bjSdf.getCalendar();
//       log.info(now.get(Calendar.YEAR) + "-"
//                + (now.get(Calendar.MONTH) + 1) + "-"
//                + now.get(Calendar.DAY_OF_MONTH)+ " "
//                +now.get(Calendar.HOUR_OF_DAY) +":"
//                +now.get(Calendar.MINUTE) + ":"
//                +now.get(Calendar.SECOND)
//                + " 周: " + now.get(Calendar.DAY_OF_WEEK) + " "+Calendar.SATURDAY);
        //System.out.println("当前时间毫秒数：" + now.getTimeInMillis());
        return now;

    }

    public static boolean isDakingTime() {
        Calendar now = getBeijingTime();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        if (hour == 8) {
            return true;
        } else if (hour == 9) {
            if (minute <= 3) {
                return true;
            }
        } else if (hour == 18) {
            if (minute >= 30) {
                return true;
            }
        } else if (hour > 18 && hour < 23) {
            return true;
        }
        return false;
    }

    public static boolean lunchApp(Context context, String packegeName){
        if (checkPackInfo(context,packegeName)) {
            openPackage(context,packegeName);
            return true;
        } else {
            return false;
        }
    }


    public static boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    public static boolean checkPackInfo(Context context, String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    public static Intent getAppOpenIntentByPackageName(Context context, String packageName){
        //Activity完整名
        String mainAct = null;
        //根据包名寻找
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);

//        @SuppressLint("WrongConstant")
        @SuppressLint("WrongConstant") List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }


}
