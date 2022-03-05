package com.clife.gradle.bean.conf;

import java.io.Serializable;

public class NexusBean implements Serializable {

    /**
     * username : clife-android
     * password : add123
     * snapshots : http://200.200.200.40:8083/nexus/content/repositories/clife-android-snapshots/
     * releaseurl : http://200.200.200.40:8083/nexus/content/repositories/clife-android-releases/
     * publicurl : http://200.200.200.40:8083/nexus/content/groups/public
     */

    private String username;
    private String password;
    private String snapshots;
    private String releaseurl;
    private String publicurl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(String snapshots) {
        this.snapshots = snapshots;
    }

    public String getReleaseurl() {
        return releaseurl;
    }

    public void setReleaseurl(String releaseurl) {
        this.releaseurl = releaseurl;
    }

    public String getPublicurl() {
        return publicurl;
    }

    public void setPublicurl(String publicurl) {
        this.publicurl = publicurl;
    }

    @Override
    public String toString() {
        return "NexusBean{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", snapshots='" + snapshots + '\'' +
                ", releaseurl='" + releaseurl + '\'' +
                ", publicurl='" + publicurl + '\'' +
                '}';
    }
}
