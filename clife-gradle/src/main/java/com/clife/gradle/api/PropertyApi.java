package com.clife.gradle.api;

import com.clife.gradle.bean.prop.PropertyBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.util.Util;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

import java.io.File;

public class PropertyApi {
    private static PropertyApi api;
    private PropertyBean property = new PropertyBean();

    public static PropertyApi getApi() {
        if (api == null) {
            synchronized (PropertyApi.class) {
                if (api == null) {
                    api = new PropertyApi();
                }
            }
        }
        return api;
    }

    public PropertyBean getProperty() {
        return property;
    }

    public void init(Project project) {
        test(project);
        ExtraPropertiesExtension extraProperties = project.getExtensions().getExtraProperties();

        if (extraProperties.has(KEY.CLIFE_GITEE_TOKEN)) {
            Object token = extraProperties.get(KEY.CLIFE_GITEE_TOKEN);
            if (token != null && token instanceof String) {
                String str = (String) token;
                property.setGiteeToken(str);
            }
        }

        if (extraProperties.has(KEY.CLIFE_LIBS_URI)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_LIBS_URI);
            if (obj2 != null && obj2 instanceof String) {
                String str = (String) obj2;
                String localPath = str;
                if (!Util.isAbsolutePath(str)) {
                    localPath = project.getRootDir().getPath() + File.separator + str;
                }
                property.setLibraryJsonUri(localPath);
            }
        }

        if (extraProperties.has(KEY.CLIFE_ALLLIBS_URI)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_ALLLIBS_URI);
            if (obj2 != null && obj2 instanceof String) {
                String str = (String) obj2;
                String localPath = str;
                if (!Util.isAbsolutePath(str)) {
                    localPath = project.getRootDir().getPath() + File.separator + str;
                }
                property.setAllLibJson(localPath);
            } else {
                property.defaultAllLibJson();
            }
        } else {
            property.defaultAllLibJson();
        }

        if (extraProperties.has(KEY.CLIFE_CONF_URI)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_CONF_URI);
            if (obj2 != null && obj2 instanceof String) {
                String str = (String) obj2;
                String localPath = str;
                if (!Util.isAbsolutePath(str)) {
                    localPath = project.getRootDir().getPath() + File.separator + str;
                }
                property.setConfJsonUri(localPath);
            } else {
                property.defaultConfJson();
            }
        } else {
            property.defaultConfJson();
        }

        if (extraProperties.has(KEY.CLIFE_SVN_USERNAME)) {
            Object uname = extraProperties.get(KEY.CLIFE_SVN_USERNAME);
            if (uname != null && uname instanceof String) {
                String str = (String) uname;
                property.setSvnUserName(str);
            }
        }

        if (extraProperties.has(KEY.CLIFE_SVN_PASSWORD)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_SVN_PASSWORD);
            if (obj2 != null && obj2 instanceof String) {
                String str = (String) obj2;
                property.setSvnPassWord(str);
            }
        }

        if (extraProperties.has(KEY.CLIFE_MAVEN_TYPE)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_MAVEN_TYPE);
            if (obj2 != null) {
                String value = obj2.toString();
                try {
                    int mType = Integer.parseInt(value);
                    property.setMavenTye(mType);
                } catch (Exception e) {
                    if (e == null)
                        return;
                    e.printStackTrace();
                }

            }
        }

        if (extraProperties.has(KEY.CLIFE_IGNORE_VERSION)) {
            Object obj2 = extraProperties.get(KEY.CLIFE_IGNORE_VERSION);
            if (obj2 != null) {
                String value = obj2.toString();
                if (value.equalsIgnoreCase("true")) {
                    property.setIgnoreVersion(true);
                } else {
                    property.setIgnoreVersion(false);
                }
            }
        }

        if (extraProperties.has(KEY.CLIFE_DEBUG)) {
            property.setDebug(true);
        } else {
            property.setDebug(false);
        }


        if (extraProperties.has(KEY.CODING_USERNAME)) {
            Object uname = extraProperties.get(KEY.CODING_USERNAME);
            if (uname != null && uname instanceof String) {
                String str = (String) uname;
                property.setCodingUsername(str);
                CodingApi.username = str;
            }
        }

        if (extraProperties.has(KEY.CODING_PASSWORD)) {
            Object uname = extraProperties.get(KEY.CODING_PASSWORD);
            if (uname != null && uname instanceof String) {
                String str = (String) uname;
                property.setCodingPassword(str);
                CodingApi.password = str;
            }
        }

    }


    private void test(Project project) {
        Object defaultConfigs = project.getExtensions().getByName("android");
        //int ded = defaultConfigs.getProperties().get("defaultConfig")
        //DefaultConfig defaultConfig = project.getExtensions().getByType(DefaultConfig.class);
        //Object defaultConfiga = project.getExtensions().getByName("defaultConfig");
        //defaultConfig.buildConfigField("String","TEST_UUXIA","this is uuxia,wanwanana");
        //System.out.println(defaultConfigs.toString());
    }

    public final class KEY {
        public final static String CLIFE_LIBS_URI = "clife.libs.uri";
        public final static String CLIFE_ALLLIBS_URI = "clife.alllibs.uri";
        public final static String CLIFE_CONF_URI = "clife.conf.uri";
        public final static String CLIFE_SVN_USERNAME = "clife.svn.username";
        public final static String CLIFE_SVN_PASSWORD = "clife.svn.password";
        public final static String CLIFE_MAVEN_TYPE = "clife.maven.type";
        public final static String CLIFE_IGNORE_VERSION = "clife.ignore.version";
        public final static String CLIFE_DEBUG = "clife.debug";
        public final static String CLIFE_GITEE_TOKEN = "clife.gitee.token";
        public final static String CODING_USERNAME = "coding.username";
        public final static String CODING_PASSWORD = "coding.password";
    }
}
