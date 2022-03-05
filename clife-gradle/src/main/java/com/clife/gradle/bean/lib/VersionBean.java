package com.clife.gradle.bean.lib;


import com.clife.gradle.util.Util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class VersionBean implements Serializable, Comparable<VersionBean> {
    private String version;
    private String description;
    private int forceUpdate;

    public VersionBean() {
    }

    public VersionBean(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        if (description == null || description.equalsIgnoreCase(""))
            return "";
        StringBuffer sb = new StringBuffer();
        String regex = "[\u003B\uFF1B]";
        List<String> result1 = Arrays.asList(description.split(regex));
        if (result1 != null || result1.size() > 0) {
            for (String s : result1) {
                sb.append("\r\n");
                sb.append("\t");
                sb.append(s);
            }
        } else {
            sb.append(description);
        }
        return sb.toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public int compareTo(VersionBean versionBean) {
        return Util.versionCompareTo(versionBean.getVersion(), getVersion()) > 0 ? -1 : 1;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", forceUpdate=" + forceUpdate +
                '}';
    }
}
