package com.clife.gradle.bean.conf;

import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.api.UrlUtil;
import com.clife.gradle.coding.CodingApi;

import java.io.Serializable;

public class ExtensionBean implements Serializable {

    /**
     * configuration : archives
     * sign : true
     * signKeyId : FB58CB54
     * signPassword : 2475431305
     * signSecertKeyRingFile :
     */

    private String configuration;
    private boolean sign;
    private String signKeyId;
    private String signPassword;
//    private String signSecertKeyRingFile ="https://gitee.com/api/v5/repos/szhittech/maven/contents/signing/secring.gpg";
    private String signSecertKeyRingFile = CodingApi.getUrl("secring.gpg");

    public void defaultSigning() {
        this.signSecertKeyRingFile = UrlUtil.toParam(signSecertKeyRingFile, PropertyApi.getApi().getProperty().getGiteeToken());
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public String getSignKeyId() {
        return signKeyId;
    }

    public void setSignKeyId(String signKeyId) {
        this.signKeyId = signKeyId;
    }

    public String getSignPassword() {
        return signPassword;
    }

    public void setSignPassword(String signPassword) {
        this.signPassword = signPassword;
    }

    public String getSignSecertKeyRingFile() {
        defaultSigning();
        return signSecertKeyRingFile;
    }

    public void setSignSecertKeyRingFile(String signSecertKeyRingFile) {
        this.signSecertKeyRingFile = signSecertKeyRingFile;
    }

    @Override
    public String toString() {
        return "ExtensionBean{" +
                "configuration='" + configuration + '\'' +
                ", sign=" + sign +
                ", signKeyId='" + signKeyId + '\'' +
                ", signPassword='" + signPassword + '\'' +
                ", signSecertKeyRingFile='" + signSecertKeyRingFile + '\'' +
                '}';
    }
}
