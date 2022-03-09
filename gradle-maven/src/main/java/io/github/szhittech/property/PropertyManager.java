package io.github.szhittech.property;

import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;

public class PropertyManager {
    private static PropertyManager instance;
    private ExtraPropertiesExtension extraProperties = null;
    private PropertiesEntity entity = null;
    private boolean isDebug = false;

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

    public boolean isDebug() {
        return isDebug;
    }

    public void init(Project project) {
        extraProperties = project.getExtensions().getExtraProperties();
        entity = new PropertiesEntity();

        isDebug = extraProperties.has("debug");

        entity.maven.name = "maven";
        if (extraProperties.has(entity.maven.name + ".username")) {
            Object string = extraProperties.get(entity.maven.name+".username");
            if (string != null && string instanceof String) {
                entity.maven.username = (String) string;
            }
        } else {
            entity.maven.username = "szhittech";
        }

        if (extraProperties.has(entity.maven.name+".password")) {
            Object string = extraProperties.get(entity.maven.name+".password");
            if (string != null && string instanceof String) {
                entity.maven.password = (String) string;
            }
        } else {
            entity.maven.password = "het123456";
        }

        if (extraProperties.has(entity.maven.name+".snapshot")) {
            Object string = extraProperties.get(entity.maven.name+".snapshot");
            if (string != null && string instanceof String) {
                entity.maven.snapshot = (String) string;
            }
        } else {
            entity.maven.snapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
        }

        if (extraProperties.has(entity.maven.name+".release")) {
            Object string = extraProperties.get(entity.maven.name+".release");
            if (string != null && string instanceof String) {
                entity.maven.release = (String) string;
            }
        } else {
            entity.maven.release = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
        }

        //私服
        if (extraProperties.has("nexus.name")) {
            Object object = extraProperties.get("nexus.name");
            if (object != null && object instanceof String) {
                entity.nexus.name = (String) object;
                if (extraProperties.has(entity.nexus.name + ".username")) {
                    Object string = extraProperties.get(entity.nexus.name + ".username");
                    if (string != null && string instanceof String) {
                        entity.nexus.username = (String) string;
                    }
                }
                if (extraProperties.has(entity.nexus.name + ".password")) {
                    Object string = extraProperties.get(entity.nexus.name + ".password");
                    if (string != null && string instanceof String) {
                        entity.nexus.password = (String) string;
                    }
                }
                if (extraProperties.has(entity.nexus.name + ".snapshot")) {
                    Object string = extraProperties.get(entity.nexus.name + ".snapshot");
                    if (string != null && string instanceof String) {
                        entity.nexus.snapshot = (String) string;
                    }
                }
                if (extraProperties.has(entity.nexus.name + ".release")) {
                    Object string = extraProperties.get(entity.nexus.name + ".release");
                    if (string != null && string instanceof String) {
                        entity.nexus.release = (String) string;
                    }
                }
            }
        }

        if (isDebug){
            Logging.getLogger(getClass()).error("maven:{}",entity.maven.toString());
            Logging.getLogger(getClass()).error("nexus:{}",entity.nexus.toString());
        }

    }


    public class PropertiesEntity {
        public MavenRepository maven = new MavenRepository();
        public MavenRepository nexus = new MavenRepository();
    }

    public class MavenRepository {
        public String name = null;
        public String snapshot = null;
        public String release = null;
        public String username = null;
        public String password = null;

        public String getUrl(String version) {
            return version.endsWith("-SNAPSHOT") ? snapshot : release;
        }

        public String geReversetUrl(String version) {
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
