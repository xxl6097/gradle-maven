package io.gradle.maven.manager;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

public class PropertyManager {
    private static PropertyManager instance;
    private ExtraPropertiesExtension extraProperties = null;
    private PropertiesEntity entity = null;
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

    public PropertiesEntity getEntity() {
        return entity;
    }

    public void init(Project project){
        extraProperties = project.getExtensions().getExtraProperties();
        entity = new PropertiesEntity();

        if (extraProperties.has("group")) {
            Object string = extraProperties.get("group");
            if (string != null && string instanceof String) {
                entity.group = (String) string;
            }
        }else{
            entity.group = "io.github.szhittech";
        }

        if (extraProperties.has("maven.username")) {
            Object string = extraProperties.get("maven.username");
            if (string != null && string instanceof String) {
                entity.maven.username = (String) string;
            }
        }else{
            entity.maven.username = "szhittech";
        }

        if (extraProperties.has("maven.password")) {
            Object string = extraProperties.get("maven.password");
            if (string != null && string instanceof String) {
                entity.maven.password = (String) string;
            }
        }else{
            entity.maven.password = "het123456";
        }

        if (extraProperties.has("maven.snapshot")) {
            Object string = extraProperties.get("maven.snapshot");
            if (string != null && string instanceof String) {
                entity.maven.snapshot = (String) string;
            }
        }else{
            entity.maven.snapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
        }

        if (extraProperties.has("maven.release")) {
            Object string = extraProperties.get("maven.release");
            if (string != null && string instanceof String) {
                entity.maven.release = (String) string;
            }
        }else{
            entity.maven.release = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
        }

        //私服
        if (extraProperties.has("nexus.username")) {
            Object string = extraProperties.get("nexus.username");
            if (string != null && string instanceof String) {
                entity.nexus.username = (String) string;
            }
        }
        if (extraProperties.has("nexus.password")) {
            Object string = extraProperties.get("nexus.password");
            if (string != null && string instanceof String) {
                entity.nexus.password = (String) string;
            }
        }
        if (extraProperties.has("nexus.snapshot")) {
            Object string = extraProperties.get("nexus.snapshot");
            if (string != null && string instanceof String) {
                entity.nexus.snapshot = (String) string;
            }
        }
        if (extraProperties.has("nexus.release")) {
            Object string = extraProperties.get("nexus.release");
            if (string != null && string instanceof String) {
                entity.nexus.release = (String) string;
            }
        }
    }


    public class PropertiesEntity{
        public String group = null;
        public MavenRepository maven = new MavenRepository();
        public MavenRepository nexus = new MavenRepository();
    }

    public class MavenRepository{
        public String snapshot = null;
        public String release = null;
        public String username = null;
        public String password = null;

        public String getUrl(String version){
            return version.endsWith("-SNAPSHOT") ? snapshot : release;
        }
        public String geReversetUrl(String version){
            return version.endsWith("-SNAPSHOT") ? release : snapshot;
        }

        @Override
        public String toString() {
            return "MavenRepository{" +
                    "snapshot='" + snapshot + '\'' +
                    ", release='" + release + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
