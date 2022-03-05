package com.clife.gradle.api;

public class UrlUtil {
    //"https://gitee.com/api/v5/repos/szhittech/maven/contents/config/maven.json
    // ?access_token=23a6e7b8814528d0e56c08f1de0e45af&ref=master";
    public static String toParam(String url,String token){
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        sb.append("?");
        sb.append("access_token=");
        sb.append(token);
        sb.append("&ref=master");
        //return sb.toString();
        return url;
    }
}
