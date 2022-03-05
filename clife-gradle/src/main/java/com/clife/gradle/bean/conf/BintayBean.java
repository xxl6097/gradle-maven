package com.clife.gradle.bean.conf;

import java.io.Serializable;

public class BintayBean implements Serializable {

    /**
     * repo : maven
     * packaging : aar
     * siteUrl : https://github.com/szhittech/pubcode
     * gitUrl : https://github.com/szhittech/pubcode.git
     * issueUrl : https://github.com/szhittech/szhittech/issues
     * vcsUrl : https://github.com/szhittech/pubcode.git
     * licenseName : Apache-2.0
     * email : xiaoli.xia@clife.cn
     * dname : xiaoli.xia
     * did : clife
     * bintrayuser : clife
     * bintrayapikey : afb255973f89db54ab4611d3bd958b56f94ab743
     */

    private String repo;
    private String packaging;
    private String siteUrl;
    private String gitUrl;
    private String issueUrl;
    private String vcsUrl;
    private String desc;
    private String licenseName;
    private String email;
    private String dname;
    private String did;
    private String bintrayuser;
    private String bintrayapikey;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getIssueUrl() {
        return issueUrl;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public String getVcsUrl() {
        return vcsUrl;
    }

    public void setVcsUrl(String vcsUrl) {
        this.vcsUrl = vcsUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getBintrayuser() {
        return bintrayuser;
    }

    public void setBintrayuser(String bintrayuser) {
        this.bintrayuser = bintrayuser;
    }

    public String getBintrayapikey() {
        return bintrayapikey;
    }

    public void setBintrayapikey(String bintrayapikey) {
        this.bintrayapikey = bintrayapikey;
    }

    @Override
    public String toString() {
        return "BintayBean{" +
                "repo='" + repo + '\'' +
                ", packaging='" + packaging + '\'' +
                ", siteUrl='" + siteUrl + '\'' +
                ", gitUrl='" + gitUrl + '\'' +
                ", issueUrl='" + issueUrl + '\'' +
                ", vcsUrl='" + vcsUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", licenseName='" + licenseName + '\'' +
                ", email='" + email + '\'' +
                ", dname='" + dname + '\'' +
                ", did='" + did + '\'' +
                ", bintrayuser='" + bintrayuser + '\'' +
                ", bintrayapikey='" + bintrayapikey + '\'' +
                '}';
    }
}
