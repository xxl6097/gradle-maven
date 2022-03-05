package com.clife.gradle.bean.prop;

import com.clife.gradle.api.UrlUtil;
import com.clife.gradle.coding.CodingApi;

import java.io.Serializable;

public class PropertyBean implements Serializable {
    //http://200.200.200.40/svn/repositories/android/code/trunk/public/sdkversion.json
    private String libraryJsonUri;
    //private String confJsonUri= "http://200.200.200.40/svn/repositories/android/code/trunk/clifepublic/maven/config/maven.json";
    private String confJsonUri = CodingApi.getUrl("maven.json");
    private String allLibJson = CodingApi.getUrl("library.json");

    private String mavenUrl;
    private String codingUsername;
    private String codingPassword;
    private String svnUserName = "eysin";
    private String svnPassWord = "eysin";
    //0:clife maven SNAPSHOT
    //1:clife maven release
    //2:sonatype SNAPSHOT
    //3:JCenter release
    private int mavenTye = 0;
    private boolean ignoreVersion = false;
    private boolean debug = false;
    private String giteeToken = "23a6e7b8814528d0e56c08f1de0e45af";

    public PropertyBean() {

    }

    public String getMavenUrl() {
        return mavenUrl;
    }

    public void setMavenUrl(String mavenUrl) {
        this.mavenUrl = mavenUrl;
    }

    public boolean isRelease() {
        if (mavenTye == 1 || mavenTye == 3) {
            return true;
        }
        return false;
    }

    public String getGiteeToken() {
        return giteeToken;
    }

    public void setGiteeToken(String giteeToken) {
        this.giteeToken = giteeToken;
    }

    public String getLibraryJsonUri() {
        return libraryJsonUri;
    }

    public void setLibraryJsonUri(String libraryJsonUri) {
        this.libraryJsonUri = libraryJsonUri;
    }

    public String getConfJsonUri() {
        return confJsonUri;
    }

    public String getAllLibJson() {
        return allLibJson;
    }

    public void setAllLibJson(String allLibJson) {
        this.allLibJson = allLibJson;
    }

    public void defaultAllLibJson() {
        this.allLibJson = UrlUtil.toParam(allLibJson, giteeToken);
    }


    public void setConfJsonUri(String confJsonUri) {
        this.confJsonUri = confJsonUri;
    }

    public void defaultConfJson() {
        this.confJsonUri = UrlUtil.toParam(confJsonUri, giteeToken);
    }

    public String getSvnUserName() {
        return svnUserName;
    }

    public void setSvnUserName(String svnUserName) {
        this.svnUserName = svnUserName;
    }

    public String getSvnPassWord() {
        return svnPassWord;
    }

    public void setSvnPassWord(String svnPassWord) {
        this.svnPassWord = svnPassWord;
    }

    public int getMavenTye() {
        return mavenTye;
    }


    public void setMavenTye(int mavenTye) {
        this.mavenTye = mavenTye;
    }

    public boolean isIgnoreVersion() {
        return ignoreVersion;
    }

    public void setIgnoreVersion(boolean ignoreVersion) {
        this.ignoreVersion = ignoreVersion;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getCodingUsername() {
        return codingUsername;
    }

    public void setCodingUsername(String codingUsername) {
        this.codingUsername = codingUsername;
    }

    public String getCodingPassword() {
        return codingPassword;
    }

    public void setCodingPassword(String codingPassword) {
        this.codingPassword = codingPassword;
    }

    @Override
    public String toString() {
        return "PropertyBean{" +
                "libraryJsonUri='" + libraryJsonUri + '\'' +
                ", confJsonUri='" + confJsonUri + '\'' +
                ", allLibJson='" + allLibJson + '\'' +
                ", mavenUrl='" + mavenUrl + '\'' +
                ", codingUsername='" + codingUsername + '\'' +
                ", codingPassword='" + codingPassword + '\'' +
                ", svnUserName='" + svnUserName + '\'' +
                ", svnPassWord='" + svnPassWord + '\'' +
                ", mavenTye=" + mavenTye +
                ", ignoreVersion=" + ignoreVersion +
                ", debug=" + debug +
                ", giteeToken='" + giteeToken + '\'' +
                '}';
    }
}
