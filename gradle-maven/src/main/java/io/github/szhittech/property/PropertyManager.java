package io.github.szhittech.property;

import io.github.szhittech.repository.RepositoryEntity;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;

public class PropertyManager {
    private static PropertyManager instance;
    private ExtraPropertiesExtension extraProperties = null;
    private boolean isDebug = false;
    private RepositoryEntity maven_release = new RepositoryEntity();
    private RepositoryEntity maven_snapshot = new RepositoryEntity();
    private RepositoryEntity nexus_snapshot = null;
    private RepositoryEntity nexus_release = null;

    public static PropertyManager getInstance() {
        if (instance == null) {
            synchronized (PropertyManager.class) {
                if (instance == null) {
                    instance = new PropertyManager();
                }
            }
        }
        return instance;
    }

    public RepositoryEntity getMaven_release() {
        return maven_release;
    }

    public RepositoryEntity getMaven_snapshot() {
        return maven_snapshot;
    }

    public RepositoryEntity getNexus_snapshot() {
        return nexus_snapshot;
    }

    public RepositoryEntity getNexus_release() {
        return nexus_release;
    }

    public boolean isDebug() {
        return isDebug;
    }

    private String getString(String key){
        if (extraProperties.has(key)) {
            Object string = extraProperties.get(key);
            if (string != null && string instanceof String) {
                return (String) string;
            }
        }
        return null;
    }

    public void init(Project project) {
        extraProperties = project.getExtensions().getExtraProperties();

        isDebug = extraProperties.has("debug");

        String maveName = "maven";
        maven_release.setName("maven");
        maven_snapshot.setName("maven");

        String destValue = "szhittech";
        String key = maveName + ".username";

        destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
        maven_release.setUsername(destValue);
        maven_snapshot.setUsername(destValue);
        destValue = null;

        destValue = "het123456";
        key = maveName + ".password";
        destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
        maven_release.setPassword(destValue);
        maven_snapshot.setPassword(destValue);
        destValue = null;

        destValue = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
        key = maveName + ".snapshot";
        destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
        maven_snapshot.setRepositoryUrl(destValue);
        destValue = null;

        destValue = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
        key = maveName + ".release";
        destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
        maven_release.setRepositoryUrl(destValue);
        destValue = null;



        //私服
        if (extraProperties.has("nexus.name")) {
            Object object = extraProperties.get("nexus.name");
            if (object != null && object instanceof String) {
                maveName = (String) object;

                key = maveName + ".username";
                destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
                if (!StringUtils.isStringEmpty(destValue)){
                    nexus_snapshot = new RepositoryEntity();
                    nexus_release = new RepositoryEntity();
                    nexus_release.setName(maveName);
                    nexus_snapshot.setName(maveName);

                    nexus_snapshot.setUsername(destValue);
                    nexus_release.setUsername(destValue);
                    destValue = null;

                    key = maveName + ".password";
                    destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
                    nexus_snapshot.setPassword(destValue);
                    nexus_release.setPassword(destValue);
                    destValue = null;

                    key = maveName + ".snapshot";
                    destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
                    nexus_snapshot.setRepositoryUrl(destValue);
                    destValue = null;

                    key = maveName + ".release";
                    destValue = StringUtils.isStringEmpty(getString(key))?destValue:getString(key);
                    nexus_release.setRepositoryUrl(destValue);
                    destValue = null;
                }


            }
        }

        if (isDebug){
            Logging.getLogger(getClass()).error("maven_release:{}",maven_release.toString());
            Logging.getLogger(getClass()).error("maven_snapshot:{}",maven_snapshot.toString());
            if (nexus_release != null){
                Logging.getLogger(getClass()).error("nexus_release:{}",nexus_release.toString());
            }
            if (nexus_snapshot != null){
                Logging.getLogger(getClass()).error("nexus_snapshot:{}",nexus_snapshot.toString());
            }
        }

    }

}
