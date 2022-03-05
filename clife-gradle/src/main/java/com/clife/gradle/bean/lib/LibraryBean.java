package com.clife.gradle.bean.lib;

import com.clife.gradle.api.ConfigApi;
import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.bean.conf.ConfigBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class LibraryBean implements Serializable {
    private String name;
    private String group;
    private String docLink;
    private List<VersionBean> release;
    private List<VersionBean> snapshot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public boolean isContain(){
        int mavenType = PropertyApi.getApi().getProperty().getMavenTye();
        if (mavenType == 1 || mavenType == 3) {
            if (release == null || release.size() == 0) {
                return false;
            }
        }else{
            if (snapshot == null || snapshot.size() == 0) {
                return false;
            }
        }
        return true;
    }


    public List<VersionBean> getRelease() {
        return release;
    }

    public void setRelease(List<VersionBean> release) {
        this.release = release;
    }

    public List<VersionBean> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(List<VersionBean> snapshot) {
        this.snapshot = snapshot;
    }

    public String getGroupName(boolean isRelease, int index) {
        VersionBean version = snapshot.get(index);
        if (isRelease) {
            version = release.get(index);
        }
        return String.format("%s:%s:%s", group, name, version.getVersion());
    }

    public String getGroupName(boolean isRelease) {
        List<VersionBean> versions = snapshot;
        if (isRelease) {
            versions = release;
        }
        if (versions == null || versions.size() == 0) {
            String msg = ",please check \"library.json\" contain " + name;
            String errMsg = isRelease?"[getGroupName]release versions is null":"snapshot versions is null";
            NullPointerException e = new NullPointerException(errMsg+msg);
            e.printStackTrace();
            throw e;
        }
        VersionBean version = Collections.max(versions);
        return String.format("%s:%s:%s", group, name, version.getVersion());
    }

    public String getGroupName() {
        int mavenType = PropertyApi.getApi().getProperty().getMavenTye();
        boolean isReleaseVersion = false;
        if (mavenType == 1 || mavenType == 3) {
            isReleaseVersion = true;
        }
        return getGroupName(isReleaseVersion);
    }

    public String getVersion() {
        int mavenType = PropertyApi.getApi().getProperty().getMavenTye();
        boolean isReleaseVersion = false;
        if (mavenType == 1 || mavenType == 3) {
            isReleaseVersion = true;
        }
        return getVersion(isReleaseVersion);
    }

    public String getVersion(boolean isRelease) {
        List<VersionBean> versions = snapshot;
        if (isRelease) {
            versions = release;
        }
        if (versions == null || versions.size() == 0) {
            String msg = ",please check \"library.json\" contain " + name;
            String errMsg = isRelease?"[getVersion]release versions is null":"snapshot versions is null";
            NullPointerException e = new NullPointerException(errMsg+msg);
            e.printStackTrace();
            throw e;
        }
        VersionBean version = Collections.max(versions);
        return version.getVersion();
    }

    public VersionBean getVersionBean(boolean isRelease) {
        List<VersionBean> versions = snapshot;
        if (isRelease) {
            versions = release;
        }
        if (versions == null || versions.size() == 0) {
            String msg = ",please check \"library.json\" contain " + name;
            String errMsg = isRelease?"[getVersionBean]release versions is null":"snapshot versions is null";
            NullPointerException e = new NullPointerException(errMsg+msg);
            e.printStackTrace();
            throw e;
        }
        VersionBean version = Collections.max(versions);
        return version;
    }

    public String getUrl() {
        if (group == null)
            return "";
        int mavenType = PropertyApi.getApi().getProperty().getMavenTye();
        ConfigBean conf = ConfigApi.getApi().getConfig();
        if (conf == null) {
            return "";
        }
        switch (mavenType) {
            case 0:
                return conf.getClife().getSnapshots() + getUrl(false);
            case 1:
                return conf.getClife().getReleaseurl() + getUrl(true);
            case 2:
                return conf.getJcenter().getSnapshots() + getUrl(false);
            case 3:
                return conf.getJcenter().getReleaseurl() + getUrl(true);
            default:
                break;
        }
        return "";
    }

    public String getUrl(boolean isRelease) {
        if (group == null)
            return "";
        String[] arr = group.split("\\.");
        StringBuffer sb = new StringBuffer();
        for (String str : arr) {
            sb.append(str);
            sb.append("/");
        }
        sb.append(name);
        sb.append("/");
        sb.append(getVersion(isRelease));
        sb.append("/");
        return sb.toString();
    }

    public String getMavenAddr(boolean isRelease) {
        if (group == null)
            return "";
        String[] arr = group.split("\\.");
        StringBuffer sb = new StringBuffer();
        for (String str : arr) {
            sb.append(str);
            sb.append("/");
        }
        sb.append(name);
        sb.append("/");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "LibraryBean{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", docLink='" + docLink + '\'' +
                ", release=" + release +
                ", snapshot=" + snapshot +
                '}';
    }
}
