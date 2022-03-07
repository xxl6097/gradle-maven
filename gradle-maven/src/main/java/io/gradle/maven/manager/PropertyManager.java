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
        if (extraProperties.has("repo.username")) {
            Object string = extraProperties.get("repo.username");
            if (string != null && string instanceof String) {
                entity.repoUserName = (String) string;
            }
        }else{
            throw new NullPointerException("please set repo.username in build.gradle");
        }

        if (extraProperties.has("repo.password")) {
            Object string = extraProperties.get("repo.password");
            if (string != null && string instanceof String) {
                entity.repoPassword = (String) string;
            }
        }else{
            throw new NullPointerException("please set repo.password in build.gradle");
        }

        if (extraProperties.has("repo.group")) {
            Object string = extraProperties.get("repo.group");
            if (string != null && string instanceof String) {
                entity.group = (String) string;
            }
        }else{
            throw new NullPointerException("please set group in build.gradle");
        }

        if (extraProperties.has("repo.snapshot")) {
            Object string = extraProperties.get("repo.snapshot");
            if (string != null && string instanceof String) {
                entity.snapshotUrl = (String) string;
            }
        }else{
            throw new NullPointerException("please set repo.snapshot in build.gradle");
        }

        if (extraProperties.has("repo.release")) {
            Object string = extraProperties.get("repo.release");
            if (string != null && string instanceof String) {
                entity.releaseUrl = (String) string;
            }
        }else{
            throw new NullPointerException("please set repo.release in build.gradle");
        }

        if (extraProperties.has("author.Id")) {
            Object string = extraProperties.get("author.Id");
            if (string != null && string instanceof String) {
                entity.authorId = (String) string;
            }
        }

        if (extraProperties.has("author.name")) {
            Object string = extraProperties.get("author.name");
            if (string != null && string instanceof String) {
                entity.authorName = (String) string;
            }
        }

        if (extraProperties.has("author.email")) {
            Object string = extraProperties.get("author.email");
            if (string != null && string instanceof String) {
                entity.authorEmail = (String) string;
            }
        }

    }

    public class PropertiesEntity{
        public String repoUserName;
        public String repoPassword;
        public String group;
        public String snapshotUrl;
        public String releaseUrl;
        public String authorId;
        public String authorName;
        public String authorEmail;
    }
}
