package com.clife.gradle.util;

import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.bean.GiteeContentBean;
import com.clife.gradle.http.SimpleHttpUtils;
import com.google.gson.Gson;

import org.gradle.api.Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static String pluginVersion = null;

    public static <T> T getGiteeFile(Project project, String url, Class<T> cls) {
        if (url == null || url.equalsIgnoreCase("")) {
            IllegalArgumentException e = new IllegalArgumentException("url is null");
            e.printStackTrace();
            throw e;
        }

        if (url.contains("200.200.200.40")) {
            return getSvnJson(url, cls);
        }

        try {
            String msg = SimpleHttpUtils.get(url);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("getGiteeFile:" + url + "\r\n" + msg);
            }
            GiteeContentBean bean = new Gson().fromJson(msg, GiteeContentBean.class);
            byte[] data = Base64.getDecoder().decode(bean.getContent());
            if (data != null && data.length > 0) {
                String json = new String(data, "utf-8");
                if (PropertyApi.getApi().getProperty().isDebug()) {
                    Logc.e("getGiteeFile:" + url + "\r\n" + json);
                }
                String path = project.getBuildDir().getPath() + File.separator + "json" + File.separator;
                if (bean != null && bean.getName() != null) {
                    if (bean.getName().contains("maven")) {
                        Util.writeFile(path,bean.getName(), bean.getContent().getBytes());
                    } else {
                        Util.writeFile(path, bean.getName(), data);
                    }
                }
                return new Gson().fromJson(json, cls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T getSvnJson(String url, Class<T> cls) {
        if (url == null || url.equalsIgnoreCase("")) {
            Exception e = new Exception("url is null");
            e.printStackTrace();
            //throw e;
        }
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("username", PropertyApi.getApi().getProperty().getSvnUserName());
            headers.put("password", PropertyApi.getApi().getProperty().getSvnPassWord());
            String msg = SimpleHttpUtils.get(url, headers);
            //Logc.e("========>>>>>>>>" + msg);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("getGiteeFile:" + url + "\r\n" + msg);
            }
            return new Gson().fromJson(msg, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downSigning(String url, String path) {
        //String url = "https://gitee.com/api/v5/repos/szhittech/maven/contents/config/maven.json?access_token=23a6e7b8814528d0e56c08f1de0e45af&ref=master";
        //String url = "https://gitee.com/api/v5/repos/szhittech/maven/contents/signing/secring.gpg?access_token=0411ccdf23ead2f6ddeb5e1678c55267&ref=master";
        if (url == null)
            return;
        if (url.contains("200.200.200.40")) {
            downSvnFile(url, path);
            return;
        }
        try {
            String msg = SimpleHttpUtils.get(url);
            GiteeContentBean bean = new Gson().fromJson(msg, GiteeContentBean.class);
            byte[] data = Base64.getDecoder().decode(bean.getContent());
            Util.writeFile(path, bean.getName(), data);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("signing file:" + path);
            }
        } catch (Exception e) {
            Logc.e("downSigning url:" + url);
            e.printStackTrace();
        }
        //String sig = "https://gitee.com/api/v5/repos/szhittech/maven/contents/signing/secring.gpg?access_token=0411ccdf23ead2f6ddeb5e1678c55267&ref=master";
    }

    public static String downSvnFile(String url, String filePath) {
        if (url == null || url.equalsIgnoreCase("")) {
            IllegalArgumentException e = new IllegalArgumentException("url is null");
            e.printStackTrace();
            throw e;
        }

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("username", PropertyApi.getApi().getProperty().getSvnUserName());
            headers.put("password", PropertyApi.getApi().getProperty().getSvnPassWord());
            String msg = SimpleHttpUtils.downFile(url, headers, filePath);
            //Logc.e("========>>>>>>>>" + msg);
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("downSvnFile:" + url + "\r\n" + msg);
            }
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isInvalidOrUnspecifiedVersion(String version) {
        if (version == null) {
            return true;
        }
        if (version.equalsIgnoreCase("unspecified")) {
            return true;
        }
        version = version == null ? "" : version.replaceAll("[^\\d\\.]+", "");
        if (version.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    /**
     * @param version1 version1
     * @param version2 version2
     * @return return value
     */
    public static int versionCompareTo(String version1, String version2) {
        if (isInvalidOrUnspecifiedVersion(version1) && isInvalidOrUnspecifiedVersion(version2))
            return 0;
        version1 = version1 == null ? "" : version1.replaceAll("[^\\d\\.]+", "");
        version2 = version2 == null ? "" : version2.replaceAll("[^\\d\\.]+", "");
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        List<Integer> version1List = new ArrayList<Integer>();
        List<Integer> version2List = new ArrayList<Integer>();
        for (int i = 0; i < version1Array.length; i++) {
            version1List.add(Integer.parseInt(version1Array[i]));
        }
        for (int i = 0; i < version2Array.length; i++) {
            version2List.add(Integer.parseInt(version2Array[i]));
        }
        int size = version1List.size() > version2List.size() ? version1List.size() : version2List.size();
        while (version1List.size() < size) {
            version1List.add(0);
        }
        while (version2List.size() < size) {
            version2List.add(0);
        }
        for (int i = 0; i < size; i++) {
            if (version1List.get(i) > version2List.get(i)) {
                return 1;
            }
            if (version1List.get(i) < version2List.get(i)) {
                return -1;
            }
        }
        return 0;
    }

    public static int ping(int timeout, String checkUrl) {
        // 个人觉得使用MIUI这个链接有失效的风险
//        final String checkUrl = "https://www.baidu.com";

        HttpURLConnection connection = null;
        try {
            URL url = new URL(checkUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setUseCaches(false);
            connection.connect();

            return connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return -1;
    }

    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equalsIgnoreCase("")) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }


    public static String readJsonData(String pactFile) {
        StringBuffer strbuffer = new StringBuffer();
        File myFile = new File(pactFile);//"D:"+File.separatorChar+"DStores.json"
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);  //new String(str,"UTF-8")
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return strbuffer.toString();
    }

    public static void saveDataToFile(String filepath, String data) {
        BufferedWriter writer = null;
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\r\nwrite file sucess,please commit svn!!!");
    }

    public static void saveDataToFile(String filepath, byte[] data) {
        BufferedWriter writer = null;
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\r\nwrite file sucess,please commit svn!!!");
    }

    /**
     * 将byte数组写入文件
     *
     * @param path path
     * @param fileName fileName
     * @param content content
     * @throws IOException IOException
     */
    public static void writeFile(String path, String fileName, byte[] content)
            throws IOException {
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(content);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isExists(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static boolean isAbsolutePath(String path) {
        if (path.startsWith("/") || path.indexOf(":") > 0) {
            return true;
        }
        return false;
    }
}
