package com.clife.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;

public class Logc {
    public static Project project;
    public static String TAG = "";//"#clife#";

    public static void e(String err) {
        /*if (project != null) {
            project.getLogger().log(LogLevel.ERROR, TAG + err);
        }*/
        System.err.println(TAG+err);
    }

    public static void i(String err) {
        /*if (project != null) {
            project.getLogger().log(LogLevel.WARN, TAG + err);
        }*/
        System.out.println(TAG+err);
    }
}
