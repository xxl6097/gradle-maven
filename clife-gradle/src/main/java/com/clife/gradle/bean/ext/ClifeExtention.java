package com.clife.gradle.bean.ext;


public class ClifeExtention {
    public String extensionName;
    public boolean ignoreError;
    public boolean ignVersionWarm;

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

    public boolean isIgnVersionWarm() {
        return ignVersionWarm;
    }

    public void setIgnVersionWarm(boolean ignVersionWarm) {
        this.ignVersionWarm = ignVersionWarm;
    }

    @Override
    public String toString() {
        return "ClifeArgsExt{" +
                "extensionName='" + extensionName + '\'' +
                ", ignoreError=" + ignoreError +
                ", ignVersionWarm=" + ignVersionWarm +
                '}';
    }
}
