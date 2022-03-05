package com.clife.gradle;

import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.bean.GiteeContentBean;
import com.clife.gradle.bean.lib.LibrariesBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.http.okhttp.HttpApi;
import com.clife.gradle.util.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws IOException {
//        String file = "/media/uuxia/SSD_FILE/code/clifepublic/maven/config/library.json";
//        String file = "/home/uuxia/Desktop/soft/code/hetgradlesdk/maven.json";
//        CodingApi.uploadFile(file);
//        CodingApi.downSigning("https://clife-devops-generic.pkg.coding.net/app-public/app-config/secring.gpg?version=latest","/home/uuxia/Desktop/");

        //CodingApi.getFile("library.json");
        testCoding();

//        String tmpFile = "/home/uuxia/Desktop/soft/code/hetgradlesdk/hetxxxsdk/build/json/library.json";
//        String json = Util.readJsonData(tmpFile);
//        System.out.println(json);
//        LibrariesBean bean = new Gson().fromJson(json, LibrariesBean.class);
//        System.out.println(bean.toString());

    }

    private static void getGiteeCodeContent(){
        String url = "https://gitee.com/api/v5/repos/szhittech/maven/contents/config/maven.json?access_token=23a6e7b8814528d0e56c08f1de0e45af&ref=master";
        //url = "https://gitee.com/api/v5/repos/szhittech/maven/contents/signing/secring.gpg?access_token=0411ccdf23ead2f6ddeb5e1678c55267&ref=master";
        //url = "http://200.200.200.40/svn/repositories/android/code/trunk/clifepublic/maven/config/maven.json";
        try {
            //String msg = SimpleHttpUtils.get(url);
            HttpApi httpApi = new HttpApi();
            String msg = httpApi.get(url);
            System.err.println(">>>>>>>>>>>>>>>>>>>>>" + msg);
            GiteeContentBean bean = new Gson().fromJson(msg, GiteeContentBean.class);
            System.err.println(">>>>>>>>>>>>>>>>>>>>>" + bean.toString());

            byte[] data = Base64.getDecoder().decode(bean.getContent());

            System.err.println(">>>>>>>>>>>>>>>>>>>>>" + new String(data));
            Util.writeFile("D:\\",bean.getName(),data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sig = "https://gitee.com/api/v5/repos/szhittech/maven/contents/signing/secring.gpg?access_token=0411ccdf23ead2f6ddeb5e1678c55267&ref=master";
    }

    public static void testCoding(){
        HttpApi httpApi = new HttpApi();
        String user = CodingApi.username;
        String pass = CodingApi.password;
        String msg = httpApi.get(user,pass,"https://clife-devops-generic.pkg.coding.net/app-public/app-config/maven.json?version=latest");
        System.err.println(">>>>>>>>>>>>>>>>>>>>>" + msg);
    }

    public static void uplaodCoding(){
        HttpApi httpApi = new HttpApi();
        String user = CodingApi.username;
        String pass = CodingApi.password;
        String cotent = Util.readJsonData("D:\\code\\clifepublic\\maven\\config\\maven.json");
        String msg = httpApi.put(user,pass,cotent,"https://clife-devops-generic.pkg.coding.net/app-public/app-config/maven.json?version=latest");
        System.err.println(">>>>>>>>>>>>>>>>>>>>>" + msg);
    }
}
