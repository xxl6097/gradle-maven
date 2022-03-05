package com.clife.gradle.bean.conf;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class PluginVersionBean implements Serializable {

    /**
     * version : 0.0.6
     * releasenode : add version maneger
     */

    private String version;
    private String releasenode;
    private int forceUpdate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getReleasenode() {
        if (releasenode == null || releasenode.equalsIgnoreCase(""))
            return "";
        StringBuffer sb = new StringBuffer();
        String regex = "[\u003B\uFF1B]";
        List<String> result1 = Arrays.asList(releasenode.split(regex));
        if (result1 != null || result1.size() > 0) {
            for (String s : result1) {
                sb.append("\r\n");
                sb.append("\t");
                sb.append(s);
            }
        } else {
            sb.append(releasenode);
        }
        return sb.toString();
    }

    public void setReleasenode(String releasenode) {
        this.releasenode = releasenode;
    }

    @Override
    public String toString() {
        return "PluginVersionBean{" +
                "version='" + version + '\'' +
                ", releasenode='" + releasenode + '\'' +
                '}';
    }
}
