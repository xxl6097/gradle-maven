package com.clife.gradle.api;

import com.clife.gradle.bean.conf.ConfigBean;
import com.clife.gradle.bean.conf.ExtensionBean;
import com.clife.gradle.bean.conf.NexusBean;
import com.clife.gradle.bean.conf.PluginVersionBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.util.Logc;
import com.clife.gradle.util.Util;
import com.google.gson.Gson;

import org.gradle.api.Project;

import java.io.File;

public class ConfigApi {
    private static ConfigApi api;
    private ConfigBean config;

    public static ConfigApi getApi() {
        if (api == null) {
            synchronized (ConfigApi.class) {
                if (api == null) {
                    api = new ConfigApi();
                }
            }
        }
        return api;
    }

    public ConfigBean getConfig() {
        return config;
    }

    public void init(Project project) {
        String filepath = PropertyApi.getApi().getProperty().getConfJsonUri();
        String mavenFilePath = project.getRootDir().getPath() + File.separator + "maven.json";
        if (Util.isExists(mavenFilePath)) {
            filepath = mavenFilePath;
        } else {
            //throw new IllegalStateException("Error:" + filepath + " does not exist");
        }

        if (PropertyApi.getApi().getProperty().isDebug()) {
            Logc.e("ConfigApi.filepath:" + filepath);
        }

        if (filepath.startsWith("http")) {
            //config = Util.getGiteeFile(project,filepath, ConfigBean.class);
            config = CodingApi.getMavenJson(project, filepath, ConfigBean.class);
            //Logc.e(WordUtil.STRING_4 + filepath);
            if (config == null) {
                NullPointerException e = new NullPointerException("maven config is null,url is " + filepath);
                e.printStackTrace();
                throw e;
            }
        } else {

            File file = new File(filepath);
            if (!file.exists()) {
                IllegalStateException e = new IllegalStateException("Error:" + file.getAbsolutePath() + " does not exist");
                e.printStackTrace();
                throw e;
            }
            String jsonString = Util.readJsonData(file.getPath());
            config = new Gson().fromJson(jsonString, ConfigBean.class);
            //Logc.e(WordUtil.STRING_7 + file.getCanonicalPath());
            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("ConfigApi.json:" + jsonString);
            }
            if (config == null) {
                NullPointerException e = new NullPointerException("maven config is null,path is " + filepath);
                e.printStackTrace();
                throw e;
            }

        }

        if (config != null) {
            String wikiAddr = config.getDocLink();
            NexusBean clifeBean = config.getClife();
            if (clifeBean != null && (clifeBean.getUsername() == null || clifeBean.getUsername().equalsIgnoreCase("")) && CodingApi.username != null && !CodingApi.username.equalsIgnoreCase("") && CodingApi.password != null && !CodingApi.password.equalsIgnoreCase("")) {
                clifeBean.setUsername(CodingApi.username);
                clifeBean.setPassword(CodingApi.password);
            }
            if (wikiAddr != null && !wikiAddr.equalsIgnoreCase("")) {
                Logc.e("public wiki:" + wikiAddr);
            }
            ExtensionBean extension = config.getExtension();
            if (extension != null) {
                boolean isSign = extension.isSign();
                String fileUrl = extension.getSignSecertKeyRingFile();
                if (isSign && fileUrl != null && !fileUrl.equalsIgnoreCase("")) {
                    //String path = Util.downSvnFile(fileUrl, project.getBuildDir().getPath() + File.separator + "signing" + File.separator);
                    CodingApi.downSigning(fileUrl, project.getBuildDir().getPath() + File.separator + "signing" + File.separator);
                    //Logc.e("download signing key file path:"+path);
                }
            }
            PluginVersionBean pVersion = config.getPlugin();
            if (pVersion != null) {
                String version = pVersion.getVersion();
                String localVersion = Util.pluginVersion;
                if (version != null && !version.equalsIgnoreCase("") && localVersion != null && !localVersion.equalsIgnoreCase("")) {
                    int p = Util.versionCompareTo(version, localVersion);
                    if (p > 0) {
                        String newVersion = "classpath 'com.github.szhittech:clife-gradle:" + version + "'";
                        String releaseNode = "\r\nrelease node:" + pVersion.getReleasenode();
                        Logc.e("Found new clife plugin Version, Please update it in root project's \"build.gradle\":\r\n" + newVersion + releaseNode);
                        if (pVersion.getForceUpdate() == 1) {
                            String msg = "force update,please update clife-gradle " + version;
                            IllegalArgumentException e = new IllegalArgumentException(msg);
                            throw e;
                        }
                    }
                }
            }

        }
    }

    public String getNexsusAddress(int mavenType) {
        if (config.getClife() == null || config.getJcenter() == null)
            return null;
        switch (mavenType) {
            case 0:
                return config.getClife().getSnapshots();
            case 1:
                return config.getClife().getReleaseurl();
            case 2:
                return config.getJcenter().getSnapshots();
            default:
                break;
        }
        return null;
    }

}
