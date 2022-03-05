package com.clife.gradle.coding;

import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.http.okhttp.HttpApi;
import com.clife.gradle.util.Logc;
import com.clife.gradle.util.Util;
import com.google.gson.Gson;

import org.gradle.api.Project;

import java.io.File;

public class CodingApi {
//    public static String username = "tianliang.liu@clife.cn";
//    public static String password = ">8wexq44buLZBfA";


//    public static String username = "public";
//    public static String password = "Het-Clife-2020";
    public static String username = "xiaoli.xia@clife.cn";
    public static String password = "het002402";
//    public static String username = "pt99vntw89l8";
//    public static String password = "8fc08a78f8a8c33644f9479aa7356f34ebefad26";

    //    public static String mavenurl = "https://clife-devops-generic.pkg.coding.net/app-public/app-config/maven.json?version=latest";
//    public static String libraryurl = "https://clife-devops-generic.pkg.coding.net/app-public/app-config/library.json?version=latest";
    public static String URL = "https://clife-devops-generic.pkg.coding.net/app-public/app-config/";

    public static String getUrl(String name) {
        return URL + name + "?version=latest";
    }

    public static void uploadFile(String filepath) {
        if (filepath == null)
            return;
        HttpApi httpApi = new HttpApi();
        String fileName = Util.getFileName(filepath);
        System.err.println(">>>>>>>>>>>>>>fileName>>>>>>>" + fileName);
        String cotent = Util.readJsonData(filepath);
        String msg = httpApi.putFile(username, password, new File(filepath), "https://clife-devops-generic.pkg.coding.net/app-public/app-config/" + fileName + "?version=latest");
        System.err.println(">>>>>>>>>>>>>>>>>>>>>" + msg);
    }

    public static void commitLibrary(String content) {
        if (content == null)
            return;
        HttpApi httpApi = new HttpApi();
        String msg = httpApi.put(username, password, content, URL + "library.json?version=latest");
        if(msg == null){
            //throw new NullPointerException("publish failed "+content);
        }else{
            //Logc.e("commit and merge library " + msg);
        }
        Logc.e("commit and merge library " + msg);
    }

    public static void getFile(String filename) {
        if (filename == null)
            return;
        HttpApi httpApi = new HttpApi();
        String fileName = Util.getFileName(filename);
        System.err.println(">>>>>>>>>>>>>>fileName>>>>>>>" + fileName);
        String msg = httpApi.get(username, password, URL + fileName + "?version=latest");
        System.err.println(">>>>>>>>>>>>>>>>>>>>>" + msg);
    }


    public static <T> T getMavenJson(Project project, String url, Class<T> cls) {
        if (url == null || url.equalsIgnoreCase("")) {
            IllegalArgumentException e = new IllegalArgumentException("url is null");
            e.printStackTrace();
            throw e;
        }

        if (url.contains("200.200.200.40")) {
            return Util.getSvnJson(url, cls);
        }

        try {
            HttpApi httpApi = new HttpApi();
            String msg = httpApi.get(username, password, url);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("getCodingFile:" + url + "\r\n" + msg);
            }
            String data = msg;
            if (data != null && data.length() > 0) {
                String json = data;
                if (PropertyApi.getApi().getProperty().isDebug()) {
                    Logc.e("getCodingFile:" + url + "\r\n" + json);
                }
                String path = project.getBuildDir().getPath() + File.separator + "json" + File.separator;
                Util.writeFile(path, "maven.json", json.getBytes());
                return new Gson().fromJson(json, cls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T getLibraryJson(Project project, String url, Class<T> cls) {
        if (url == null || url.equalsIgnoreCase("")) {
            IllegalArgumentException e = new IllegalArgumentException("url is null");
            e.printStackTrace();
            throw e;
        }

        if (url.contains("200.200.200.40")) {
            return Util.getSvnJson(url, cls);
        }

        try {
            HttpApi httpApi = new HttpApi();
            String msg = null;
            if (url.contains("coding.net")) {
                msg = httpApi.get(username, password, url);
            } else {
                msg = httpApi.get(url);
            }
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("getCodingFile:" + url + "\r\n" + msg);
            }
            String data = msg;
            if (data != null && data.length() > 0) {
                String json = data;
                if (PropertyApi.getApi().getProperty().isDebug()) {
                    Logc.e("getCodingFile:" + url + "\r\n" + json);
                }
                String path = project.getBuildDir().getPath() + File.separator + "json" + File.separator;
                Util.writeFile(path, "library.json", json.getBytes());
                return new Gson().fromJson(json, cls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void downSigning(String url, String path) {
        if (url == null)
            return;
        if (url.contains("200.200.200.40")) {
            Util.downSvnFile(url, path);
            return;
        }
        try {
            HttpApi httpApi = new HttpApi();
            byte[] data = httpApi.getBytes(username, password, url);
            Util.writeFile(path, "secring.gpg", data);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("signing file:" + path);
            }
        } catch (Exception e) {
            Logc.e("downSigning url:" + url);
            e.printStackTrace();
        }
    }

}
