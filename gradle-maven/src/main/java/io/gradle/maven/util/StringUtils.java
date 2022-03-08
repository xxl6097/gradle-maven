package io.gradle.maven.util;

public class StringUtils {
    public static boolean isStringEmpty(String s) {
        return s == null || "".equalsIgnoreCase(s.trim());
    }
    public static boolean isSnapshot(String version){
        return version.endsWith("-SNAPSHOT") ? true : false;
    }
}
